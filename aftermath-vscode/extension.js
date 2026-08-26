const vscode = require('vscode');

function activate(context) {
    console.log('AFTERMATH VS Code extension is active');

    let listCmd = vscode.commands.registerCommand('aftermath.listIncidents', async function () {
        vscode.window.showInformationMessage('Fetching AFTERMATH production incidents from http://localhost:8090...');
    });

    let attachCmd = vscode.commands.registerCommand('aftermath.attach', async function () {
        const workspaceFolders = vscode.workspace.workspaceFolders;
        if (!workspaceFolders) {
            vscode.window.showErrorMessage('No open project folder found in VS Code');
            return;
        }
        vscode.window.showInformationMessage('Attaching AFTERMATH SDK to project: ' + workspaceFolders[0].uri.fsPath);
    });

    context.subscriptions.push(listCmd, attachCmd);
}

function deactivate() {}

module.exports = {
    activate,
    deactivate
};
