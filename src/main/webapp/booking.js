function getApiUrl(endpoint) {
  const currentPath = window.location.pathname;
  if (currentPath.includes('/PETCARE-1.0.0/')) {
    return '/PETCARE-1.0.0' + endpoint;
  }
  return endpoint;
}

document.addEventListener('DOMContentLoaded', function () {
  const form = document.getElementById('crudForm');
  const tableBody = document.getElementById('recordTableBody');
  let editRow = null;
  let editId = null;
  
  checkAuth();
  loadBookings();
  
  const urlParams = new URLSearchParams(window.location.search);
  const serviceId = urlParams.get('serviceId');
  const serviceName = urlParams.get('serviceName');
  
  if (serviceId) {
    document.getElementById('serviceId').value = serviceId;
  }
  
  if (serviceName) {
    const heading = document.querySelector('h2');
    heading.textContent = `BOOKING - ${serviceName}`;
  }
  
  async function checkAuth() {
    try {
      const response = await fetch(getApiUrl('/whoami'));
      const data = await response.json();
      
      if (!data.loggedIn) {
        window.location.href = 'signin.html?returnUrl=booking.html';
        return;
      }
      
      const authLinks = document.getElementById('authLinks');
      if (authLinks && data.user) {
        authLinks.innerHTML = `
          <li class="si"><span style="color: #618264;">Welcome, ${data.user}</span></li>
          <li class="si"><a href="#" onclick="logout(); return false;">Logout</a></li>
        `;
      }
    } catch (error) {
      console.error('Auth check error:', error);
      window.location.href = 'signin.html?returnUrl=booking.html';
    }
  }
  
  async function loadBookings() {
    try {
      const response = await fetch(getApiUrl('/booking'));
      if (response.ok) {
        const data = await response.json();
        if (data.bookings && Array.isArray(data.bookings)) {
          data.bookings.forEach(booking => {
            addRecordToTable(booking.id, booking.name, booking.email, booking.number, booking.petType);
          });
        }
      }
    } catch (error) {
      console.error('Error loading bookings:', error);
    }
  }
  
	form.addEventListener('submit', function (event) {
			event.preventDefault();

			const name = document.getElementById('name').value;
			const email = document.getElementById('email').value;
			const number = document.getElementById('number').value;
			const petType = document.getElementById('petType').value;
			const serviceId = document.getElementById('serviceId').value || '0';
			

			if (editRow && editId) {
					fetch(getApiUrl('/booking'), {
							method: 'PUT',
							headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
							body: new URLSearchParams({ id: editId, name, email, number, petType, serviceId })
					}).then(r => r.json())
					  .then(data => {
						if (data && data.status === 'ok') {
							editRow.cells[0].textContent = name;
							editRow.cells[1].textContent = email;
							editRow.cells[2].textContent = number;
							editRow.cells[3].textContent = petType;
							editRow = null;
							editId = null;
							form.reset();
						} else {
							alert('Failed to update booking');
						}
					  }).catch(err => {
						console.error(err);
						alert('Error updating booking');
					  });
					return;
			}

			fetch(getApiUrl('/booking'), {
					method: 'POST',
					headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
					body: new URLSearchParams({ name, email, number, petType, serviceId })
			}).then(r => r.json())
				.then(data => {
					if (data && data.status === 'ok') {
						addRecordToTable(data.id, name, email, number, petType);
					} else {
						alert(data.message || 'Failed to save booking');
					}
					form.reset();
				}).catch(err => {
					console.error(err);
					alert('Error saving booking');
					form.reset();
				});
	});

  function addRecordToTable(id, name, email, number, petType) {
	  const newRow = document.createElement('tr');
	  newRow.dataset.id = id;
	  newRow.innerHTML = `
		  <td>${name}</td>
		  <td>${email}</td>
		  <td>${number}</td>
		  <td>${petType}</td>
		  <td>
			  <button class="edit">Edit</button>
			  <button class="delete">Delete</button>
		  </td>
	  `;
	  tableBody.appendChild(newRow);

	  newRow.querySelector('.edit').addEventListener('click', function () {
		  const rowData = newRow.querySelectorAll('td');
		  document.getElementById('name').value = rowData[0].textContent;
		  document.getElementById('email').value = rowData[1].textContent;
		  document.getElementById('number').value = rowData[2].textContent;
		  document.getElementById('petType').value = rowData[3].textContent;
          
		  editRow = newRow;
		  editId = id;
	  });

	  newRow.querySelector('.delete').addEventListener('click', function () {
		  if (confirm('Are you sure you want to delete this booking?')) {
			  fetch(getApiUrl('/booking'), {
				  method: 'DELETE',
				  headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
				  body: new URLSearchParams({ id: id })
			  }).then(r => r.json())
				.then(data => {
				  if (data && data.status === 'ok') {
					  tableBody.removeChild(newRow);
				  } else {
					  alert('Failed to delete booking');
				  }
				}).catch(err => {
				  console.error(err);
				  alert('Error deleting booking');
				});
		  }
	  });
  }
});

function logout() {
  fetch(getApiUrl('/logout'))
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

