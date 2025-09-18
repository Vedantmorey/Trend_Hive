document.addEventListener('DOMContentLoaded', () => {
    // === Get the JWT token from local storage ===
    const token = localStorage.getItem('accessToken');
    if (!token) {
        alert('You are not logged in. Redirecting to login page.');
        window.location.href = '../Login/R-Login.html';
        return;
    }

    // === Initial page load ===
    fetchAndRenderRetailerProducts();

    // === Add/Edit Product Form Logic ===
    const addProductForm = document.getElementById('addProductForm');
    addProductForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const productId = document.getElementById('productId').value;
        
        if (productId) {
            await updateProduct(productId, token);
        } else {
            await addProduct(token);
        }
    });

    // === Dashboard Navigation Logic ===
    const sidebarLinks = document.querySelectorAll('.sidebar ul li a');
    const dashboardSections = document.querySelectorAll('.dashboard-section');

    sidebarLinks.forEach(link => {
        link.addEventListener('click', (event) => {
            event.preventDefault();
            const targetSectionId = event.target.getAttribute('data-section');
            
            dashboardSections.forEach(section => {
                section.classList.remove('active');
            });
            document.getElementById(targetSectionId).classList.add('active');
        });
    });

    // === Logout Button Logic ===
    const logoutBtn = document.getElementById('logoutBtn');
    logoutBtn.addEventListener('click', () => {
        localStorage.removeItem('accessToken');
        alert('You have been logged out.');
        window.location.href = '../Login/R-Login.html';
    });
});

// --- API Calls and Rendering ---

async function fetchAndRenderRetailerProducts() {
    const token = localStorage.getItem('accessToken');
    if (!token) return;

    try {
        const response = await fetch('http://localhost:8080/api/products/retailer', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) throw new Error('Failed to fetch your products.');
        
        const products = await response.json();
        renderProductsTable(products);
    } catch (error) {
        console.error('Error fetching products:', error);
        alert(error.message);
    }
}

function renderProductsTable(products) {
    const productTableBody = document.getElementById('productTableBody');
    productTableBody.innerHTML = '';

    if (products.length === 0) {
        const row = productTableBody.insertRow();
        row.innerHTML = `<td colspan="5" style="text-align: center;">No products added yet.</td>`;
        return;
    }

    products.forEach(product => {
        const row = productTableBody.insertRow();
        row.innerHTML = `
            <td><img src="${product.image_url}" alt="${product.productName}" style="width: 50px;"></td>
            <td>${product.productName}</td>
            <td>$${product.price.toFixed(2)}</td>
            <td>${product.category}</td>
            <td>
                <button onclick="editProduct(${product.id})" class="edit-btn">Edit</button>
                <button onclick="deleteProduct(${product.id})" class="delete-btn">Delete</button>
            </td>
        `;
    });
}

// === EDIT FUNCTIONALITY ===

async function editProduct(productId) {
    const token = localStorage.getItem('accessToken');
    if (!token) return;

    try {
        const response = await fetch(`http://localhost:8080/api/products/${productId}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) throw new Error('Failed to fetch product details for editing.');

        const product = await response.json();
        populateFormForEdit(product);
    } catch (error) {
        console.error('Error on edit:', error);
        alert(error.message);
    }
}

function populateFormForEdit(product) {
    document.getElementById('productId').value = product.id;
    document.getElementById('productName').value = product.productName;
    document.getElementById('productDesc').value = product.description;
    document.getElementById('productPrice').value = product.price;
    document.getElementById('productCategory').value = product.category;
    document.getElementById('productQuantity').value = product.quantity;

    document.getElementById('submitBtn').textContent = 'Update Product';
}

async function updateProduct(productId, token) {
    const formData = new FormData();

    const productDetails = {
        productName: document.getElementById('productName').value,
        description: document.getElementById('productDesc').value,
        price: parseFloat(document.getElementById('productPrice').value),
        category: document.getElementById('productCategory').value,
        quantity: parseInt(document.getElementById('productQuantity').value)
    };

    formData.append('product', new Blob([JSON.stringify(productDetails)], { type: 'application/json' }));
    
    const imageFile = document.getElementById('productImage').files[0];
    if (imageFile) {
        formData.append('image', imageFile);
    }

    try {
        const response = await fetch(`http://localhost:8080/api/products/${productId}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });

        if (!response.ok) throw new Error('Failed to update product.');
        
        alert('Product updated successfully!');
        resetForm();
    } catch (error) {
        console.error('Error updating product:', error);
        alert(error.message);
    }
}

// === DELETE FUNCTIONALITY ===

async function deleteProduct(productId) {
    const token = localStorage.getItem('accessToken');
    if (!token) return;

    if (!confirm('Are you sure you want to delete this product?')) {
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/products/${productId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) throw new Error('Failed to delete product.');

        alert('Product deleted successfully!');
        fetchAndRenderRetailerProducts();
    } catch (error) {
        console.error('Error deleting product:', error);
        alert(error.message);
    }
}

// === ADD FUNCTIONALITY ===

async function addProduct(token) {
    const formData = new FormData();
        
    const productDetails = {
        productName: document.getElementById('productName').value,
        description: document.getElementById('productDesc').value,
        price: parseFloat(document.getElementById('productPrice').value),
        category: document.getElementById('productCategory').value,
        quantity: parseInt(document.getElementById('productQuantity').value)
    };
    
    formData.append('product', new Blob([JSON.stringify(productDetails)], { type: 'application/json' }));
    
    const imageFile = document.getElementById('productImage').files[0];
    formData.append('image', imageFile);

    try {
        const response = await fetch('http://localhost:8080/api/products', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });

        if (response.ok) {
            alert('Product added successfully!');
            resetForm();
        } else {
            const errorData = await response.json();
            alert('Failed to add product: ' + (errorData.message || 'Please try again.'));
        }
    } catch (error) {
        console.error('Error adding product:', error);
        alert('An error occurred. Please check the console.');
    }
}

function resetForm() {
    document.getElementById('addProductForm').reset();
    document.getElementById('productId').value = '';
    document.getElementById('submitBtn').textContent = 'Add Product';
    fetchAndRenderRetailerProducts();
}