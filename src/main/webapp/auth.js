// Admin users list - add usernames here who should have admin access
const ADMIN_USERS = ['arnubdatta', 'admin'];

// Check if a username is an admin
function isAdmin(username) {
    return ADMIN_USERS.includes(username.toLowerCase());
}

// Get current user info
async function getCurrentUser() {
    try {
        const response = await fetch('/PETCARE-1.0.0/whoami');
        const data = await response.json();
        return data.loggedIn ? data : null;
    } catch (error) {
        console.error('Error fetching user info:', error);
        return null;
    }
}

// Initialize auth UI - call this on page load
async function initAuthUI() {
    const user = await getCurrentUser();
    
    // Update auth links in header
    const authLinks = document.getElementById('authLinks');
    if (authLinks) {
        if (user && user.loggedIn) {
            authLinks.innerHTML = `
                <li class="si"><span style="color: #618264;">Hi, ${user.user}</span></li>
                <li class="si"><a href="#" onclick="logout(); return false;">Logout</a></li>
            `;
        } else {
            authLinks.innerHTML = `
                <li class="si"><a href="signin.html">Sign in</a></li>
                <li class="si"><a href="signup.html">Sign Up</a></li>
            `;
        }
    }
    
    // Show/hide admin link based on user role
    const adminLink = document.querySelector('a[href="admin.html"]');
    if (adminLink) {
        const parentLi = adminLink.closest('li');
        if (user && user.loggedIn && isAdmin(user.user)) {
            // User is admin - show the link
            if (parentLi) parentLi.style.display = 'list-item';
        } else {
            // Not admin - hide the link
            if (parentLi) parentLi.style.display = 'none';
        }
    }
    
    return user;
}

// Logout function
function logout() {
    fetch('/PETCARE-1.0.0/logout')
        .then(r => r.json())
        .then(data => {
            if (data.status === 'ok') {
                window.location.href = 'index.html';
            }
        })
        .catch(err => {
            console.error('Logout error:', err);
            window.location.href = 'index.html';
        });
}

// Protect admin pages - call this on admin.html
async function requireAdmin() {
    const user = await getCurrentUser();
    
    if (!user || !user.loggedIn) {
        alert('Please sign in to access this page');
        window.location.href = 'signin.html?returnUrl=admin.html';
        return false;
    }
    
    if (!isAdmin(user.user)) {
        alert('Access denied. Admin privileges required.');
        window.location.href = 'service.html';
        return false;
    }
    
    return true;
}
