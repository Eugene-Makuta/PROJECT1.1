async function register() {
  const name = document.getElementById("name").value;
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;
  const message = document.getElementById("message");

  if (!name || !email || !password) {
    message.textContent = "All fields are required!";
    return;
  }

  try {
    const res = await fetch("https://your-backend.onrender.com/auth/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password, role: "ROLE_USER" })
    });

    if (res.ok) {
      alert("Registered successfully! Please login.");
      window.location.href = "index.html";
    } else {
      const text = await res.text();
      message.textContent = text || "Registration failed.";
    }

  } catch (err) {
    message.textContent = "Server error. Try again later.";
  }
}
