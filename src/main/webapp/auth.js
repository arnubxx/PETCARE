const ADMIN_USERS = ['arnubdatta', 'admin'];

function getContextPath() {
    const path = window.location.pathname;
    const parts = path.split('/');
    return parts.length > 1 && parts[1] ? '/' + parts[1] : '';
}

function isAdmin(username) {
    return ADMIN_USERS.includes(username.toLowerCase());
}

async function getCurrentUser() {
    try {
        const contextPath = getContextPath();
        const response = await fetch(`${contextPath}/whoami`);
        const data = await response.json();
        return data.loggedIn ? data : null;
    } catch (error) {
        console.error('Error fetching user info:', error);
        return null;
    }
}

async function initAuthUI() {
    const user = await getCurrentUser();
    
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
    
    const adminLink = document.querySelector('a[href="admin.html"]');
    if (adminLink) {
        const parentLi = adminLink.closest('li');
        if (user && user.loggedIn && isAdmin(user.user)) {
            if (parentLi) parentLi.style.display = 'list-item';
        } else {
            if (parentLi) parentLi.style.display = 'none';
        }
    }
    
    return user;
}

function logout() {
    const contextPath = getContextPath();
    fetch(`${contextPath}/logout`)
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
