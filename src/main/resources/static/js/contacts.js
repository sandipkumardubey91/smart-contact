// console.log("Contacts.js");
// const baseURL = "http://localhost:8081";
// const baseURL = "http://192.168.29.94:8081";
// const baseURL = "https://www.scm20.site";
const viewContactModal = document.getElementById("view_contact_modal");

// options with default values
const options = {
  placement: "bottom-right",
  backdrop: true,
  backdropClasses: "bg-gray-900/30 dark:bg-gray-900/80 inset-0 fixed z-30",
  closable: true,
  onHide: () => {
    console.log("modal is hidden");
  },
  onShow: () => {
    setTimeout(() => {
      if (viewContactModal) {
        viewContactModal.classList.add("scale-100");
      }
    }, 50);
  },
  onToggle: () => {
    console.log("modal has been toggled");
  },
};

// instance options object
const instanceOptions = {
  id: "view_contact_modal",
  override: true,
};

const contactModal = new Modal(viewContactModal, options, instanceOptions);

function openContactModal() {
  contactModal.show();
}

function closeContactModal() {
  contactModal.hide();
}

async function loadContactdata(id) {
  //function call to load data
  console.log(id);
  try {
    const data = await (await fetch(`/api/contacts/${id}`)).json();
    console.log(data);
    document.querySelector("#contact_name").innerHTML = data.name;
    document.querySelector("#contact_email").innerHTML = data.email;
    document.querySelector("#contact_image").src = data.picture;
    document.querySelector("#contact_address").innerHTML = data.address;
    document.querySelector("#contact_phone").innerHTML = data.phoneNumber;
    document.querySelector("#contact_about").innerHTML = data.description;
    const contactFavorite = document.querySelector("#contact_favorite");
    if (data.favorite) {
      contactFavorite.innerHTML =
        "<i class='fas fa-star text-yellow-400'></i><i class='fas fa-star text-yellow-400'></i><i class='fas fa-star text-yellow-400'></i><i class='fas fa-star text-yellow-400'></i><i class='fas fa-star text-yellow-400'></i>";
    } else {
      contactFavorite.innerHTML = "Not Favorite Contact";
    }

    document.querySelector("#contact_website").href = data.websiteLink;
    document.querySelector("#contact_website").innerHTML = data.websiteLink;
    document.querySelector("#contact_linkedIn").href = data.linkedInLink;
    document.querySelector("#contact_linkedIn").innerHTML = data.linkedInLink;
    // alert("contact modal");
    openContactModal();
  } catch (error) {
    console.log("Error: ", error);
  }
}

//delete contact

async function deleteContact(id) {
  Swal.fire({
    title: "Do you want to delete the contact?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Delete",
    cancelButtonText: "Cancel",
    buttonsStyling: false,
    customClass: {
      popup:
        "bg-gray-200 shadow-lg dark:bg-gray-700 dark:text-gray-200 pb-10 rounded-lg",
      actions: "flex gap-4 justify-center mt-6", // added top margin here
      confirmButton:
        "bg-red-700 hover:bg-red-800 text-white font-bold py-2 px-4 rounded",
      cancelButton:
        "bg-green-700 hover:bg-green-800 text-white font-bold py-2 px-4 rounded",
    },
  }).then((result) => {
    if (result.isConfirmed) {
      const url = `${baseURL}/user/contacts/delete/` + id;
      console.log(url);
      window.location.replace(url);
    }
  });
}

async function deleteAllContacts() {
  Swal.fire({
    title: "Do you want to delete the contact?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Delete",
    cancelButtonText: "Cancel",
    buttonsStyling: false,
    customClass: {
      popup:
        "bg-gray-200 shadow-lg dark:bg-gray-700 dark:text-gray-200 pb-10 rounded-lg",
      actions: "flex gap-4 justify-center mt-6", // added top margin here
      confirmButton:
        "bg-red-700 hover:bg-red-800 text-white font-bold py-2 px-4 rounded",
      cancelButton:
        "bg-green-700 hover:bg-green-800 text-white font-bold py-2 px-4 rounded",
    },
  }).then((result) => {
    if (result.isConfirmed) {
      const url = `${baseURL}/user/contacts/delete/all`;
      console.log(url);
      window.location.replace(url);
    }
  });
}
