// TQMS Frontend Interactivity

document.addEventListener('DOMContentLoaded', () => {
    fetchDashboardSummary();
});

async function fetchDashboardSummary() {
    try {
        const res = await fetch('/api/dashboard/summary');
        if (res.ok) {
            const data = await res.json();
            document.getElementById('stat-total').innerText = data.totalRecords;
            document.getElementById('stat-waiting').innerText = data.waitingCount;
            document.getElementById('stat-called').innerText = data.calledCount;
            document.getElementById('stat-consultation').innerText = data.inConsultationCount;
            document.getElementById('stat-completed').innerText = data.completedCount;
        }
    } catch (err) {
        console.error('Failed to load summary stats', err);
    }
}

async function registerPatient(e) {
    e.preventDefault();
    const patientName = document.getElementById('patientName').value.trim();
    const contact = document.getElementById('contact').value.trim();
    const department = document.getElementById('department').value;
    const priority = document.getElementById('priority').value;

    const payload = {
        patientName,
        contact,
        department,
        priority
    };

    try {
        const res = await fetch('/api/records', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            const created = await res.json();
            alert(`Patient registered successfully! Assigned Token: ${created.tokenNo}`);
            document.getElementById('register-form').reset();
            window.location.reload();
        } else {
            alert('Failed to register patient. Please verify input data.');
        }
    } catch (err) {
        alert('Network error while registering patient: ' + err.message);
    }
}

async function updateStatus(id, newStatus) {
    try {
        const res = await fetch(`/api/records/${id}/status?status=${newStatus}`, {
            method: 'PATCH'
        });

        if (res.ok) {
            window.location.reload();
        } else {
            const err = await res.json();
            alert('Cannot change status: ' + (err.error || 'Transition not allowed.'));
        }
    } catch (err) {
        alert('Error updating status: ' + err.message);
    }
}

async function lookupToken() {
    const token = document.getElementById('token-input').value.trim();
    const resultBox = document.getElementById('lookup-result');

    if (!token) {
        resultBox.style.display = 'block';
        resultBox.innerHTML = '<span style=\"color: #dc2626;\">Please enter a valid token number.</span>';
        return;
    }

    try {
        const res = await fetch(`/api/public/status/${encodeURIComponent(token)}`);
        if (res.ok) {
            const data = await res.json();
            resultBox.style.display = 'block';
            resultBox.innerHTML = `
                <strong>Token:</strong> ${data.tokenNo} &bull; 
                <strong>Patient:</strong> ${data.patientName} &bull; 
                <strong>Department:</strong> ${data.department}<br>
                <strong>Current Status:</strong> <span class=\"badge status-${data.status.toLowerCase()}\">${data.status}</span> &bull; 
                <strong>Patients Ahead:</strong> <strong>${data.patientsAhead}</strong>
            `;
        } else {
            resultBox.style.display = 'block';
            resultBox.innerHTML = `<span style=\"color: #dc2626;\">Token <strong>${token}</strong> not found. Please check and retry.</span>`;
        }
    } catch (err) {
        resultBox.style.display = 'block';
        resultBox.innerHTML = '<span style=\"color: #dc2626;\">Error querying server status.</span>';
    }
}

function filterQueue() {
    const query = document.getElementById('search-input').value.toLowerCase();
    const statusFilter = document.getElementById('status-filter').value;
    const rows = document.querySelectorAll('#queue-tbody tr:not(#empty-queue-row)');

    rows.forEach(row => {
        const text = row.innerText.toLowerCase();
        const matchesQuery = !query || text.includes(query);
        const matchesStatus = !statusFilter || text.includes(statusFilter.toLowerCase());
        row.style.display = (matchesQuery && matchesStatus) ? '' : 'none';
    });
}
