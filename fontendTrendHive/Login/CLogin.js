document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');

    loginForm.addEventListener('submit', async (event) => {
        // Prevent the form from reloading the page
        event.preventDefault();

        // 1. Get credentials from the form
        const email = document.getElementById('username').value; // Your input id is "username"
        const password = document.getElementById('password').value;

        // 2. Create a JSON object for the backend
        const loginData = {
            email: email,
            password: password
        };

        try {
            // 3. Send the credentials to your backend login API
            const response = await fetch('http://localhost:8080/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(loginData)
            });

            // 4. Handle the response
            if (response.ok) {
                const data = await response.json();
                
                // --- IMPORTANT ---
                // Store the JWT in the browser's local storage
                localStorage.setItem('accessToken', data.accessToken);
                
                alert('Login successful!');
                
                // Redirect to the customer dashboard
                window.location.href = '../CustomerDashboard/index.html';
            } else {
                alert('Login failed. Please check your email and password.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred during login. Please try again.');
        }
    });
});