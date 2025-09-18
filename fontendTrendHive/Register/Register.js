document.addEventListener('DOMContentLoaded', () => {
    const registerForm = document.getElementById('registerForm');

    registerForm.addEventListener('submit', async (event) => {
        // Prevent the default form submission which reloads the page
        event.preventDefault();

        // 1. Get data from the form inputs
        const fullName = document.getElementById('fullname').value;
        const email = document.getElementById('email').value;
        const phone = document.getElementById('phone').value;
        const password = document.getElementById('password').value;
        
        // Find the selected role from the radio buttons
        const selectedRole = document.querySelector('input[name="role"]:checked').value;

        // 2. Create a JSON object that matches your backend RegistrationDto
        const registrationData = {
            fullName: fullName,
            email: email,
            phone: phone,
            password: password,
            role: selectedRole
        };

        try {
            // 3. Send the data to the backend API using fetch
            const response = await fetch('http://localhost:8080/api/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(registrationData)
            });

            // 4. Handle the response from the server
            if (response.ok) {
                // If registration is successful (e.g., status 201 Created)
                alert('Registration successful! Please log in.');
                // Redirect to the appropriate login page
                if (selectedRole === 'RETAILER') {
                    window.location.href = '../Login/R-Login.html';
                } else {
                    window.location.href = '../Login/C-Login.html';
                }
            } else {
                // If there's an error (e.g., email already exists)
                const errorData = await response.json();
                alert('Registration failed: ' + (errorData.message || 'Please try again.'));
            }
        } catch (error) {
            // Handle network errors
            console.error('Error:', error);
            alert('An error occurred. Please check the console and try again.');
        }
    });
});