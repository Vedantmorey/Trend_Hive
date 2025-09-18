document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
        // Instead of an alert, we can show a message in the cart itself.
        const cartContainer = document.getElementById('cart-items-container');
        cartContainer.innerHTML = `
            <div class="cart-empty">
                <h3>Please Log In</h3>
                <p>You must be logged in to view your cart.</p>
                <a href="../Login/C-Login.html" class="checkout-btn" style="text-decoration: none; display: inline-block; width: auto; padding: 10px 20px; margin-top: 15px;">Login Now</a>
            </div>`;
        document.querySelector('.cart-summary-card').style.display = 'none';
        return;
    }

    fetchAndRenderCart();

    const checkoutBtn = document.querySelector('.checkout-btn');
    if (checkoutBtn) {
        checkoutBtn.addEventListener('click', () => {
            const totalAmountText = document.getElementById('total-amount').textContent;
            const total = parseFloat(totalAmountText.replace('₹', '').trim());
            if (total > 0) {
                window.location.href = 'payment.html';
            } else {
                showToast("Your cart is empty!", 'error');
            }
        });
    }
});

async function fetchAndRenderCart() {
    const cartContainer = document.getElementById('cart-items-container');
    const subtotalEl = document.getElementById('subtotal-amount');
    const totalAmountEl = document.getElementById('total-amount');
    const itemCountEl = document.getElementById('item-count');
    const token = localStorage.getItem('accessToken');

    cartContainer.innerHTML = '<div class="cart-loading"><p>Loading your cart...</p></div>';

    try {
        const response = await fetch('http://localhost:8080/api/cart', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error("Failed to fetch cart data.");
        }

        const cart = await response.json();
        
        cartContainer.innerHTML = ''; // Clear loading state

        if (!cart.items || cart.items.length === 0) {
            cartContainer.innerHTML = '<div class="cart-empty"><h3>Your cart is empty</h3><p>Looks like you haven\'t added anything yet.</p></div>';
            itemCountEl.textContent = '0';
            subtotalEl.textContent = '₹0.00';
            totalAmountEl.textContent = '₹0.00';
            return;
        }

        let subtotal = 0;
        cart.items.forEach(item => {
            const itemSubtotal = item.price * item.quantity;
            subtotal += itemSubtotal;
            
            const cartItemCard = document.createElement('div');
            cartItemCard.classList.add('cart-item-card');
            cartItemCard.innerHTML = `
                <div class="product-image">
                    <img src="${item.imageUrl || './images/placeholder.png'}" alt="${item.productName}">
                </div>
                <div class="product-details">
                    <h4>${item.productName}</h4>
                    <p class="price">₹${item.price.toFixed(2)}</p>
                </div>
                <div class="quantity-control">
                    <button class="qty-btn" onclick="updateQty(${item.productId}, ${item.quantity - 1})">-</button>
                    <span>${item.quantity}</span>
                    <button class="qty-btn" onclick="updateQty(${item.productId}, ${item.quantity + 1})">+</button>
                </div>
                <div class="subtotal">
                    ₹${itemSubtotal.toFixed(2)}
                </div>
                <div class="actions">
                    <button class="remove-btn" onclick="removeItem(${item.productId})">&times;</button>
                </div>
            `;
            cartContainer.appendChild(cartItemCard);
        });

        subtotalEl.textContent = `₹${subtotal.toFixed(2)}`;
        totalAmountEl.textContent = `₹${subtotal.toFixed(2)}`;
        itemCountEl.textContent = cart.items.length;

    } catch (error) {
        console.error('Error fetching cart:', error);
        showToast(error.message, 'error');
        cartContainer.innerHTML = '<div class="cart-empty"><h3>Error</h3><p>Could not load your cart. Please try again later.</p></div>';
    }
}

async function updateQty(productId, newQuantity) {
    if (newQuantity <= 0) {
        removeItem(productId);
        return;
    }
    await updateCartAPI('PUT', `http://localhost:8080/api/cart/update?productId=${productId}&quantity=${newQuantity}`, 'Cart updated!');
}

async function removeItem(productId) {
    await updateCartAPI('DELETE', `http://localhost:8080/api/cart/remove/${productId}`, 'Item removed from cart!');
}

async function updateCartAPI(method, url, successMessage) {
    const token = localStorage.getItem('accessToken');
    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || "Failed to update cart.");
        }
        
        showToast(successMessage);
        fetchAndRenderCart(); // Refresh the cart view

    } catch (error) {
        console.error(`Error with ${method} request:`, error);
        showToast(error.message, 'error');
    }
}

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    if(!toast) return;
    toast.textContent = message;
    toast.className = 'toast show ' + type;
    
    setTimeout(() => {
        toast.className = toast.className.replace('show', '');
    }, 3000);
}
