document.addEventListener('DOMContentLoaded', () => {
    // Find the login form on the page
    const loginForm = document.getElementById('loginForm');

    // Add an event listener for when the form is submitted
    loginForm.addEventListener('submit', async (event) => {
        // Prevent the form from its default behavior (reloading the page)
        event.preventDefault();

        // Get the email and password from the input fields
        const email = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        // Prepare the data to send to the backend
        const loginData = {
            email: email,
            password: password
        };

        try {
            // Send the login request to your backend API
            const response = await fetch('http://localhost:8080/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(loginData)
            });

            // Check if the login was successful
            if (response.ok) {
                const data = await response.json();
                
                localStorage.setItem('accessToken', data.accessToken);

                const decodedToken = jwt_decode(data.accessToken);
                const userRole = decodedToken.roles; 

                alert('Login successful!');

                // Redirect the user based on their role
                if (userRole === 'RETAILER') {
                    window.location.href = '../RetailerDashboard/seller.html';
                } else {
                    window.location.href = '../CustomerDashboard/index.html';
                }

            } else {
                // If login fails, show an error message
                alert('Login failed. Please check your email and password.');
            }
        } catch (error) {
            // Handle network or other unexpected errors
            console.error('Error:', error);
            alert('An error occurred during login. Please try again.');
        }
    });
});