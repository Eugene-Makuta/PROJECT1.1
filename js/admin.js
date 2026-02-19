async function loadItems() {
  const token = localStorage.getItem("token");
  if (!token) {
    alert("Please login as admin first.");
    window.location.href = "index.html";
    return;
  }

  try {
    const res = await fetch("https://your-backend.onrender.com/admin/all-items", {
      headers: { "Authorization": "Bearer " + token }
    });

    if (!res.ok) throw new Error("Failed to fetch items");

    const items = await res.json();
    const container = document.getElementById("items");

    container.innerHTML = items.map(item => `
      <div class="bg-white p-4 rounded shadow">
        <h3 class="font-bold text-lg">${item.name}</h3>
        <p><strong>Status:</strong> ${item.status}</p>
        <p><strong>Location:</strong> ${item.location}</p>
        <p><strong>Reported At:</strong> ${new Date(item.reportedAt).toLocaleString()}</p>
        <p><strong>Reward:</strong> ${item.rewardAmount || 0}</p>
        <img src="https://your-backend.onrender.com/${item.qrCodePath}" width="100" class="mt-2"/>
      </div>
    `).join("");

  } catch (err) {
    alert("Error loading items. Make sure you are admin.");
    console.error(err);
  }
}

// Load items immediately
loadItems();
