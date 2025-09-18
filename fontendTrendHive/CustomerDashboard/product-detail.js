document.addEventListener('DOMContentLoaded', async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');
    const detailContainer = document.getElementById('productDetailContainer');

    if (!productId) {
        detailContainer.innerHTML = '<p>Product not found. Please go back and select a product.</p>';
        return;
    }

    detailContainer.innerHTML = '<p>Loading product details...</p>';

    try {
        const response = await fetch(`http://localhost:8080/api/products/${productId}`);
        if (!response.ok) {
            throw new Error('Product not found or server error.');
        }
        const product = await response.json();
        renderProductDetails(product);
    } catch (error) {
        console.error('Error fetching product:', error);
        detailContainer.innerHTML = `<p>Error: Could not load product details. Please try again later.</p>`;
    }
});

function renderProductDetails(product) {
    const detailContainer = document.getElementById('productDetailContainer');

    const imageUrl = product.image_url || './images/placeholder.png';
    const productName = product.productName || 'Unnamed Product';
    const productPrice = product.price != null ? `₹${product.price.toFixed(2)}` : 'Price not available';
    const productDesc = product.description || 'No description available for this product.';
    const inStock = product.quantity > 0;
    const stockText = inStock ? `${product.quantity} units available` : 'Out of Stock';

    detailContainer.innerHTML = `
        <div class="product-detail-layout">
            <div class="product-image-gallery">
                <img src="${imageUrl}" alt="${productName}">
            </div>
            <div class="product-info">
                <h1>${productName}</h1>
                <p class="price">${productPrice}</p>
                <p class="description">${productDesc}</p>
                <div class="availability">
                    <span class="${inStock ? 'in-stock' : 'out-of-stock'}">${stockText}</span>
                </div>
                <div class="purchase-actions">
                    <div class="quantity-selector">
                        <button class="qty-btn" id="decrease-qty">-</button>
                        <input type="number" id="quantity-input" value="1" min="1" max="${product.quantity}">
                        <button class="qty-btn" id="increase-qty">+</button>
                    </div>
                    <button class="add-to-cart-btn" data-product-id="${product.id}" ${!inStock ? 'disabled' : ''}>
                        ${inStock ? 'Add to Cart' : 'Out of Stock'}
                    </button>
                </div>
            </div>
        </div>
    `;

    setupEventListeners(product);
}

function setupEventListeners(product) {
    const addToCartBtn = document.querySelector('.add-to-cart-btn');
    const decreaseBtn = document.getElementById('decrease-qty');
    const increaseBtn = document.getElementById('increase-qty');
    const quantityInput = document.getElementById('quantity-input');
    
    // Add to Cart Logic
    if (addToCartBtn) {
        addToCartBtn.addEventListener('click', () => {
            const quantity = parseInt(quantityInput.value, 10);
            addItemToCart(product.id, quantity);
        });
    }

    // Quantity selector logic
    decreaseBtn.addEventListener('click', () => {
        let currentValue = parseInt(quantityInput.value, 10);
        if (currentValue > 1) {
            quantityInput.value = currentValue - 1;
        }
    });

    increaseBtn.addEventListener('click', () => {
        let currentValue = parseInt(quantityInput.value, 10);
        if (currentValue < product.quantity) {
            quantityInput.value = currentValue + 1;
        }
    });

    quantityInput.addEventListener('change', () => {
        let currentValue = parseInt(quantityInput.value, 10);
        if (currentValue > product.quantity) {
            quantityInput.value = product.quantity;
        } else if (currentValue < 1) {
            quantityInput.value = 1;
        }
    });
}

// --- Helper Functions (Now local to this page) ---

async function addItemToCart(productId, quantity) {
    const url = `http://localhost:8080/api/cart/add?productId=${productId}&quantity=${quantity}`;
    const token = localStorage.getItem('accessToken'); 
    
    if (!token) {
        showToast('You must be logged in to add items to your cart.', 'error');
        return;
    }

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Failed to add item to cart.');
        }

        showToast('Item added to cart!');
        // Optionally update cart badge, though it would require more logic to fetch count
    } catch (error) {
        console.error('Error adding to cart:', error);
        showToast(error.message, 'error');
    }
}

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    if (!toast) return;
    toast.textContent = message;
    toast.className = 'toast show ' + type;
    
    setTimeout(() => {
        toast.className = toast.className.replace('show', '');
    }, 3000);
}
