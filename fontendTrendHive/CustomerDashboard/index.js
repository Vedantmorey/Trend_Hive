document.addEventListener('DOMContentLoaded', () => {
    fetchAndInitializeHomepage();
    initializeCarouselSafely();

    const searchBtn = document.getElementById('searchBtn');
    const searchInput = document.getElementById('searchInput');

    searchBtn.addEventListener('click', () => {
        const query = searchInput.value.trim();
        fetchAndRenderProducts({ query });
    });

    searchInput.addEventListener('keypress', (event) => {
        if (event.key === 'Enter') {
            const query = searchInput.value.trim();
            fetchAndRenderProducts({ query });
        }
    });

    const navHome = document.getElementById('navHome');
    if (navHome) {
        navHome.addEventListener('click', (event) => {
            event.preventDefault();
            fetchAndRenderProducts();
        });
    }
});

async function fetchAndInitializeHomepage() {
    try {
        const response = await fetch('http://localhost:8080/api/products');
        if (!response.ok) {
            throw new Error('Failed to fetch initial data');
        }
        const products = await response.json();
        
        fetchAndRenderProducts({ products });
        initializeCategorySuggestBox(products);

    } catch (error) {
        console.error('Error initializing homepage:', error);
        document.getElementById('emptyState').textContent = 'Failed to load products. Please try again later.';
        document.getElementById('emptyState').style.display = 'block';
    }
}

async function fetchAndRenderProducts(params = {}) {
    const productsContainer = document.getElementById('categorySections');
    const emptyState = document.getElementById('emptyState');
    let productsToRender = params.products;

    if (!productsToRender) {
        let url = 'http://localhost:8080/api/products';
        const queryString = new URLSearchParams(params).toString();
        if (queryString) url += `?${queryString}`;
    
        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error('Failed to fetch products');
            productsToRender = await response.json();
        } catch (error) {
            console.error('Error fetching products:', error);
            emptyState.textContent = 'Failed to load products. Please try again later.';
            emptyState.style.display = 'block';
            return;
        }
    }

    productsContainer.innerHTML = '';
    emptyState.style.display = 'none';

    if (productsToRender.length === 0) {
        emptyState.style.display = 'block';
        return;
    }

    renderProducts(productsToRender);
}

/**
 * Renders a list of products onto the page.
 */
function renderProducts(products) {
    const productsContainer = document.getElementById('categorySections');
    
    products.forEach(product => {
        const productCard = document.createElement('div');
        productCard.classList.add('product-card');

        // FIXED: Using the correct field name from the backend entity
        const imageUrl = product.image_url || './images/placeholder.png';
        const productName = product.productName || 'Unnamed Product';
        const productPrice = product.price !== null && typeof product.price !== 'undefined' ? product.price.toFixed(2) : 'N/A';

        productCard.innerHTML = `
            <a href="product-detail.html?id=${product.id}">
                <img src="${imageUrl}" alt="${productName}">
                <h4>${productName}</h4>
                <p class="price">$${productPrice}</p>
            </a>
            <button class="add-to-cart-btn" data-product-id="${product.id}">Add to Cart</button>
        `;
        
        const addToCartBtn = productCard.querySelector('.add-to-cart-btn');
        addToCartBtn.addEventListener('click', (event) => {
            event.preventDefault();
            addItemToCart(product.id, 1);
        });
        
        productsContainer.appendChild(productCard);
    });
}

function initializeCategorySuggestBox(products) {
    const categoryInput = document.getElementById('categoryInput');
    const categorySuggestions = document.getElementById('categorySuggestions');
    
    const allCategories = [...new Set(products.map(p => p.category).filter(Boolean))];

    categoryInput.addEventListener('input', () => {
        const query = categoryInput.value.toLowerCase();
        categorySuggestions.innerHTML = '';
        
        if (query.length > 0) {
            const matches = allCategories.filter(category => category.toLowerCase().includes(query));
            
            if (matches.length > 0) {
                matches.forEach(category => {
                    const suggestionDiv = document.createElement('div');
                    suggestionDiv.textContent = category;
                    suggestionDiv.addEventListener('click', () => {
                        categoryInput.value = category;
                        categorySuggestions.classList.remove('show');
                        fetchAndRenderProducts({ category });
                    });
                    categorySuggestions.appendChild(suggestionDiv);
                });
                categorySuggestions.classList.add('show');
            } else {
                categorySuggestions.classList.remove('show');
            }
        } else {
            categorySuggestions.classList.remove('show');
        }
    });
    
    document.addEventListener('click', (event) => {
        if (!categoryInput.contains(event.target) && !categorySuggestions.contains(event.target)) {
            categorySuggestions.classList.remove('show');
        }
    });
}

function initializeCarouselSafely() {
    const slides = document.querySelectorAll('.carousel-slide');
    const dots = document.querySelectorAll('.dot');
    if (!slides.length || !dots.length) return;

    let currentSlide = 0;
    const slideInterval = 5000;

    function showSlide(index) {
        slides.forEach(slide => slide.classList.remove('active'));
        dots.forEach(dot => dot.classList.remove('active'));
        slides[index].classList.add('active');
        dots[index].classList.add('active');
    }

    function nextSlide() {
        currentSlide = (currentSlide + 1) % slides.length;
        showSlide(currentSlide);
    }

    setInterval(nextSlide, slideInterval);

    dots.forEach((dot, index) => {
        dot.addEventListener('click', () => {
            currentSlide = index;
            showSlide(currentSlide);
        });
    });
}


async function addItemToCart(productId, quantity) {
    const url = `http://localhost:8080/api/cart/add?productId=${productId}&quantity=${quantity}`;
    
    const token = localStorage.getItem('accessToken'); 
    
    if (!token) {
        showToast('You must be logged in as a customer to add items to your cart.', 'error');
        return;
    }

    try {
        console.log(`Attempting to add product ID: ${productId} with quantity: ${quantity}`);
        console.log(`Sending POST request to: ${url}`);
        
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        console.log('Received response from server:', response);

        if (!response.ok) {
            console.error('Server responded with an error status:', response.status, response.statusText);
            const errorData = await response.json();
            console.error('Error data from server:', errorData);
            throw new Error(errorData.message || `Server error: ${response.status}`);
        }

        const cartDto = await response.json();
        console.log('Successfully added to cart:', cartDto);
        showToast('Item added to cart!');
        updateCartBadge(cartDto.totalItems);

    } catch (error) {
        // This will catch network errors (like CORS) or errors thrown from a bad response
        console.error('Caught a critical error in addItemToCart:', error);
        showToast(error.message, 'error');
    }
}

function updateCartBadge(totalItems) {
    const cartBadge = document.getElementById('cartBadge');
    if (totalItems > 0) {
        cartBadge.textContent = totalItems;
        cartBadge.style.display = 'inline-block';
    } else {
        cartBadge.style.display = 'none';
    }
}

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = 'toast show ' + type;
    
    setTimeout(() => {
        toast.className = toast.className.replace('show', '');
    }, 3000);
}


