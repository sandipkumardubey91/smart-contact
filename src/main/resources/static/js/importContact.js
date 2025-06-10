function openImportModal() {
  const modal = document.getElementById("view_import_modal");
  if (modal) {
    modal.classList.remove("hidden");
  }
}

function closeContactModal() {
  const modal = document.getElementById("view_import_modal");
  if (modal) {
    modal.classList.add("hidden");
  }
}
