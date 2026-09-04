const API_URL = "http://localhost:8080/api/projects";


// ===============================
// ELEMENTS
// ===============================

const projectList = document.getElementById("projectList");
const projectCount = document.getElementById("projectCount");

const refreshBtn = document.getElementById("refreshBtn");

const problemInput = document.getElementById("problemInput");
const analyzeBtn = document.getElementById("analyzeBtn");

const aiResult = document.getElementById("aiResult");
const analysisMessage = document.getElementById("analysisMessage");

const instanceCount = document.getElementById("instanceCount");
const scalingStrategy = document.getElementById("scalingStrategy");
const priority = document.getElementById("priority");


// ===============================
// LOAD PROJECTS
// ===============================

async function loadProjects() {

    try {

        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error("Failed to load projects");
        }

        const projects = await response.json();

        displayProjects(projects);

        projectCount.textContent = projects.length;

    } catch (error) {

        console.error(error);

        projectList.innerHTML = `
            <div class="loading">
                Unable to connect to CloudForge backend.
            </div>
        `;
    }
}


// ===============================
// DISPLAY PROJECTS
// ===============================

function displayProjects(projects) {

    if (projects.length === 0) {

        projectList.innerHTML = `
            <div class="loading">
                No projects yet.
            </div>
        `;

        return;
    }

    projectList.innerHTML = "";

    projects.forEach(project => {

        const card = document.createElement("div");

        card.className = "project-card";

        card.innerHTML = `

            <div class="project-card-header">

                <h3>${escapeHtml(project.name)}</h3>

                <span class="project-status">
                    ${escapeHtml(project.status || "CREATED")}
                </span>

            </div>

            <p>
                ${escapeHtml(
                    project.description ||
                    "No description provided."
                )}
            </p>

            <div class="project-meta">

                <small>
                    Project ID: ${project.id}
                </small>

                <div class="project-actions">

                    <button onclick="editProject(${project.id})">
                        Edit
                    </button>

                    <button onclick="deleteProject(${project.id})">
                        Delete
                    </button>

                </div>

            </div>
        `;

        projectList.appendChild(card);

    });

}


// ===============================
// CREATE PROJECT
// ===============================

async function createProject(name, description) {

    try {

        const response = await fetch(API_URL, {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({
                name: name,
                description: description
            })

        });

        if (!response.ok) {
            throw new Error("Failed to create project");
        }

        await loadProjects();

    } catch (error) {

        console.error(error);

        alert("Unable to create project.");

    }

}


// ===============================
// EDIT PROJECT
// ===============================

async function editProject(id) {

    const name = prompt(
        "Enter the new project name:"
    );

    if (!name) {
        return;
    }

    const description = prompt(
        "Enter the new project description:"
    );

    if (!description) {
        return;
    }

    try {

        const response = await fetch(
            `${API_URL}/${id}`,
            {
                method: "PUT",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    name: name,
                    description: description
                })
            }
        );

        if (!response.ok) {
            throw new Error(
                "Failed to update project"
            );
        }

        await loadProjects();

    } catch (error) {

        console.error(error);

        alert("Unable to update project.");

    }

}


// ===============================
// DELETE PROJECT
// ===============================

async function deleteProject(id) {

    const confirmed = confirm(
        "Are you sure you want to delete this project?"
    );

    if (!confirmed) {
        return;
    }

    try {

        const response = await fetch(
            `${API_URL}/${id}`,
            {
                method: "DELETE"
            }
        );

        if (!response.ok) {
            throw new Error(
                "Failed to delete project"
            );
        }

        await loadProjects();

    } catch (error) {

        console.error(error);

        alert("Unable to delete project.");

    }

}


// ===============================
// AI ANALYSIS - API 1
// ===============================

async function analyzeProblem() {

    const problem = problemInput.value.trim();

    if (!problem) {

        alert(
            "Describe your cloud problem first."
        );

        return;
    }

    analyzeBtn.textContent = "Analyzing...";
    analyzeBtn.disabled = true;

    try {

        const response = await fetch(
            "http://localhost:8080/api/ai/analyze",
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    projectId: 1,
                    problem: problem
                })
            }
        );

        if (!response.ok) {

            throw new Error(
                `AI analysis failed: ${response.status}`
            );
        }

        const result = await response.json();

        console.log(
            "CloudForge AI analysis:",
            result
        );

        // Handle ApiResponse wrapper if present
        const data =
            result.data ||
            result.result ||
            result;

        // ===============================
        // DISPLAY AI ANALYSIS
        // ===============================

        instanceCount.textContent =
            data.instances ??
            data.recommendedInstances ??
            "—";

        scalingStrategy.textContent =
            data.scalingStrategy ||
            data.strategy ||
            (
                data.loadBalancer
                    ? "Horizontal Scaling + Load Balancer"
                    : "Standard Scaling"
            );

        priority.textContent =
            data.priority ||
            "Medium";

        analysisMessage.textContent =
            data.recommendation ||
            data.analysis ||
            data.message ||
            data.suggestion ||
            "Infrastructure analysis completed.";

        // Show result
        aiResult.classList.remove("hidden");

        // Scroll to result
        aiResult.scrollIntoView({
            behavior: "smooth",
            block: "center"
        });

    } catch (error) {

        console.error(
            "CloudForge AI Error:",
            error
        );

        alert(
            "CloudForge could not connect to the AI service."
        );

    } finally {

        analyzeBtn.textContent =
            "Analyze Problem";

        analyzeBtn.disabled = false;
    }
}


// ===============================
// SUGGESTION BUTTONS
// ===============================

document.querySelectorAll(
    ".suggestion"
).forEach(button => {

    button.addEventListener(
        "click",
        () => {

            const text =
                button.textContent.toLowerCase();

            if (text.includes("traffic")) {

                problemInput.value =
                    "My website is getting a lot of traffic and the server is becoming slow.";

            }

            else if (text.includes("slow")) {

                problemInput.value =
                    "My application response time is becoming very slow.";

            }

            else if (text.includes("cost")) {

                problemInput.value =
                    "My cloud infrastructure is becoming too expensive. Help me reduce the cost.";

            }

            else if (text.includes("scale")) {

                problemInput.value =
                    "My application needs to scale automatically when traffic increases.";

            }

            problemInput.focus();

        }
    );

});


// ===============================
// ANALYZE BUTTON
// ===============================

analyzeBtn.addEventListener(
    "click",
    analyzeProblem
);


// ===============================
// REFRESH PROJECTS
// ===============================

refreshBtn.addEventListener(
    "click",
    loadProjects
);


// ===============================
// APPROVE BUTTON
// ===============================

document.getElementById(
    "approveBtn"
).addEventListener(
    "click",
    () => {

        alert(
            "Infrastructure automation will be connected to Terraform in the next CloudForge phase."
        );

    }
);


// ===============================
// BASIC HTML ESCAPING
// ===============================

function escapeHtml(value) {

    const div =
        document.createElement("div");

    div.textContent = value;

    return div.innerHTML;
}


// ===============================
// INITIAL LOAD
// ===============================

loadProjects();