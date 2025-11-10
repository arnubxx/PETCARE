document.addEventListener('DOMContentLoaded', function () {
  const form = document.getElementById('crudForm');
  const tableBody = document.getElementById('recordTableBody');
  let editRow = null;
  
    form.addEventListener('submit', function (event) {
            event.preventDefault();

            const name = document.getElementById('name').value;
            const email = document.getElementById('email').value;
            const number = document.getElementById('number').value;
            const petType = document.getElementById('petType').value;

            // If editing locally, update row and do not POST as new
            if (editRow) {
                    editRow.cells[0].textContent = name;
                    editRow.cells[1].textContent = email;
                    editRow.cells[2].textContent = number;
                    editRow.cells[3].textContent = petType;
                    editRow = null;
                    form.reset();
                    return;
            }

            // POST to backend
            fetch('/booking', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: new URLSearchParams({ name, email, number, petType })
            }).then(r => r.json())
                .then(data => {
                    if (data && data.status === 'ok') {
                        addRecord(name, email, number, petType);
                    } else {
                        alert('Failed to save booking');
                    }
                    form.reset();
                }).catch(err => {
                    console.error(err);
                    alert('Error saving booking');
                    form.reset();
                });
    });

  function addRecord(name, email, number, petType) {
      const newRow = document.createElement('tr');
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
      });

      newRow.querySelector('.delete').addEventListener('click', function () {
          tableBody.removeChild(newRow);
      });
  }
});
