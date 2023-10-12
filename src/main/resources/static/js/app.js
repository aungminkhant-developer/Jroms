const dropdowns = document.querySelectorAll(".dropdown");
dropdowns.forEach(dropdown => {
    const select = dropdown.querySelector(".select");
    const menu = dropdown.querySelector(".menu");
    const options = dropdown.querySelectorAll(".menu li");
    const selected = dropdown.querySelector(".selected");

    select.onclick = () => {
        select.classList.toggle("select-clicked");
        menu.classList.toggle("menu-open");
    }

    options.forEach(option => option.onclick = () => {
        selected.innerText = option.innerText;
        select.classList.remove("select-clicked");
        menu.classList.remove("menu-open");
        options.forEach(opt => opt.classList.remove("active-li"));
        option.classList.add("active-li");
    })
})

// document.onclick = (e) => {
//     if(e.target.classList.contains("select") || e.target.classList.contains("menu")) {
//        return; 
//     }

//     document.querySelectorAll(".menu").forEach(menu => menu.classList.remove("menu-open"));
//     document.querySelectorAll(".select").forEach(select => select.classList.remove("select-clicked"));
// }