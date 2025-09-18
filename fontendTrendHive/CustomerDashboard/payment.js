document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
        alert("You must be logged in to complete the payment.");
        window.location.href = 'index.html';
        return;
    }
    fetchCartTotal();
    setupEventListeners();
});

async function fetchCartTotal() {
    const totalAmountEl = document.getElementById('totalAmount');
    const token = localStorage.getItem('accessToken');
    try {
        const response = await fetch('http://localhost:8080/api/cart', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) throw new Error("Failed to fetch cart data.");
        const cart = await response.json();
        if (cart.items.length === 0) {
            totalAmountEl.textContent = '₹0.00';
            const confirmBtn = document.getElementById('confirmBtn');
            confirmBtn.disabled = true;
            confirmBtn.textContent = 'Your Cart is Empty';
        } else {
            let total = 0;
            cart.items.forEach(item => { total += item.price * item.quantity; });
            totalAmountEl.textContent = `₹${total.toFixed(2)}`;
        }
    } catch (error) {
        console.error('Error fetching cart total:', error);
        totalAmountEl.textContent = 'Error';
        showToast(error.message, 'error');
    }
}

function setupEventListeners() {
    const confirmBtn = document.getElementById('confirmBtn');
    if (confirmBtn) {
        confirmBtn.addEventListener('click', placeOrder);
    }
}

/**
 * --- MOCK BACKEND FUNCTION ---
 * This function simulates a call to your backend server.
 * It waits for 1 second and then returns a success response.
 * This temporarily fixes the 404 error for development purposes.
 */
async function mockPlaceOrderAPI(token, paymentMethod) {
    console.log("Simulating backend call to place order...");
    
    // Simulate a network delay of 1 second (1000 milliseconds)
    await new Promise(resolve => setTimeout(resolve, 1000));

    if (!token) {
        // Simulate an authentication error if no token is provided
        return {
            ok: false,
            json: () => Promise.resolve({ message: "Authentication failed." })
        };
    }

    console.log(`Order placed with method: ${paymentMethod}`);
    
    // Simulate a successful response
    return {
        ok: true,
        json: () => Promise.resolve({ message: "Order placed successfully!" })
    };
}

async function placeOrder() {
    const selectedMethod = document.querySelector('input[name="payMethod"]:checked');
    if (!selectedMethod) {
        showToast('Please select a payment method.', 'error');
        return;
    }

    const token = localStorage.getItem('accessToken');
    const confirmBtn = document.getElementById('confirmBtn');
    confirmBtn.textContent = 'Placing Order...';
    confirmBtn.disabled = true;

    try {
        // --- IMPORTANT ---
        // The real fetch call is commented out. We are using our MOCK function instead.
        const response = await mockPlaceOrderAPI(token, selectedMethod.value);
        /*
        // This is the REAL code to use when your backend is ready.
        const response = await fetch('http://localhost:8080/api/orders/place', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ paymentMethod: selectedMethod.value })
        });
        */

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Failed to place order.');
        }

        const paymentSection = document.getElementById('paymentSection');
        paymentSection.innerHTML = `
            <h2>Order Placed!</h2>
            <p>Thank you for your purchase.</p>
            <a href="index.html" class="home-btn" style="margin-top: 20px;">Continue Shopping</a>
        `;

    } catch (error) {
        console.error('Error placing order:', error);
        showToast(error.message, 'error');
        confirmBtn.textContent = 'Confirm Order';
        confirmBtn.disabled = false;
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

