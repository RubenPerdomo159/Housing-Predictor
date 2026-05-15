async function loadProperties() {
    const params = new URLSearchParams();

    const minPrice = document.getElementById("minPrice").value;
    const maxPrice = document.getElementById("maxPrice").value;
    const minSize = document.getElementById("minSize").value;
    const maxSize = document.getElementById("maxSize").value;
    const rooms = document.getElementById("rooms").value;
    const type = document.getElementById("type").value;
    const zone = document.getElementById("zone").value;
    const valuationStatus = document.getElementById("valuationStatus").value.trim().toLowerCase();

    if (minPrice) params.append("minPrice", minPrice);
    if (maxPrice) params.append("maxPrice", maxPrice);
    if (minSize) params.append("minSize", minSize);
    if (maxSize) params.append("maxSize", maxSize);
    if (rooms) params.append("rooms", rooms);
    if (type) params.append("type", type);
    if (zone) params.append("zone", zone);

    console.log("Filtro seleccionado:", valuationStatus);

    const baseList = await fetch("/api/properties?" + params.toString())
        .then(res => res.json());

    const validList = baseList.filter(item => item.payload.propertyCode);

    if (!valuationStatus) {
        renderTable(validList);
        return;
    }

    const fullData = await Promise.all(
        validList.map(async item => {
            try {
                const full = await fetch(`/api/property/${item.payload.propertyCode}/full`);
                if (!full.ok) throw new Error("Error en /full");

                const json = await full.json();

                return {
                    base: item,
                    status: json.valuation.status.trim().toLowerCase()
                };
            } catch (err) {
                console.warn("Saltando propiedad con error:", item.payload.propertyCode);
                return null;
            }
        })
    );

    const cleaned = fullData.filter(x => x !== null);

    const filtered = cleaned
        .filter(x => x.status === valuationStatus)
        .map(x => x.base);

    console.log("Coinciden:", filtered.length);

    renderTable(filtered);
}

function renderTable(data) {
    const tableBody = document.querySelector("#properties-table tbody");
    tableBody.innerHTML = "";

    data.forEach(item => {
        const p = item.payload;

        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${p.price}</td>
            <td>${p.size}</td>
            <td>${p.rooms}</td>
            <td>${p.address}</td>
        `;

        row.onclick = () => showDetails(p.propertyCode);
        row.style.cursor = "pointer";

        tableBody.appendChild(row);
    });
}

function showDetails(propertyCode) {
    fetch(`/api/property/${propertyCode}/full`)
        .then(res => res.json())
        .then(data => renderDetails(data))
        .catch(err => console.error("ERROR:", err));
}

function renderDetails(data) {
    const container = document.getElementById("details");

    const p = data.details;
    const valuation = data.valuation;

    container.innerHTML = `
        <h2>Ficha detallada</h2>

        <p><strong>Dirección:</strong> ${p.address}</p>
        <p><strong>Precio real:</strong> ${p.price} €</p>
        <p><strong>Precio estimado:</strong> ${valuation.expectedPrice.toFixed(0)} €</p>
        <p><strong>Diferencia:</strong> ${valuation.difference.toFixed(0)} €</p>
        <p><strong>Estado:</strong> ${valuation.status}</p>

        <h3>Explicación del modelo</h3>
        <p>${data.explanation}</p>

        <h3>Propiedades comparables</h3>
        <div id="comparables"></div>
    `;

    loadComparables(p.propertyCode);
}

function loadComparables(propertyCode) {
    fetch(`/api/property/${propertyCode}/comparables`)
        .then(res => res.json())
        .then(data => renderComparables(data));
}

function renderComparables(list) {
    const container = document.getElementById("comparables");

    if (list.length === 0) {
        container.innerHTML = "<p>No hay comparables disponibles.</p>";
        return;
    }

    let html = `
        <table id="comparables-table">
            <thead>
                <tr>
                    <th>Precio</th>
                    <th>Metros</th>
                    <th>Hab</th>
                    <th>Dirección</th>
                </tr>
            </thead>
            <tbody>
    `;

    list.forEach(item => {
        const p = item.payload || item.details || item.property || item;

        html += `
            <tr style="cursor:pointer" onclick="showDetails('${p.propertyCode}')">
                <td>${p.price}</td>
                <td>${p.size}</td>
                <td>${p.rooms}</td>
                <td>${p.address}</td>
            </tr>
        `;
    });

    html += `
            </tbody>
        </table>
    `;

    container.innerHTML = html;
}

document.addEventListener("DOMContentLoaded", loadProperties);
