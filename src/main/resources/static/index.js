const spinner = document.getElementById("spinner");
const input = document.querySelector("input");
const submitButton = document.querySelector("button");
const resultField = document.getElementById("result");

submitButton.onclick = async e => {
    e.preventDefault();
    const number = input.value;
    if (number) {
        spinner.classList.add("show");
        const result = await fetch(`/api/${number}`);
        const json = await result.json();
        const {id, value, status} = json;
        if (status === "CALCULATED") {
            resultField.innerHTML = `The ${id}. prime is <strong>${value}</strong>`;
            spinner.classList.remove("show");
        }
    }
};
