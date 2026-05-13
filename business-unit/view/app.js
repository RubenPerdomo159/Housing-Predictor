function loadProperties() {
    fetch("http://localhost:7000/properties")
        .then(res => res.json())
        .then(data => {
            const tableBody = document.querySelector("#properties-table tbody");
            tableBody.innerHTML = "";

            data.forEach(item => {
                const p = item.payload; // más limpio

                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${p.price}</td>
                    <td>${p.size}</td>
                    <td>${p.rooms}</td>
                    <td>${p.address}</td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(err => console.error("Error cargando propiedades:", err));
}
