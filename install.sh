#!/usr/bin/env bash
# AFTERMATH 1-Click Installer for Linux / macOS
set -e

echo "=========================================================================="
echo "⚡ AFTERMATH 1-CLICK CLI INSTALLER (LINUX / macOS)"
echo "=========================================================================="

INSTALL_DIR="$HOME/.aftermath/bin"
mkdir -p "$INSTALL_DIR"

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
CLI_JAR="$SCRIPT_DIR/aftermath-cli/target/aftermath-cli-0.1.0-SNAPSHOT.jar"

if [ -f "$CLI_JAR" ]; then
    cp "$CLI_JAR" "$INSTALL_DIR/aftermath-cli.jar"
    echo "✅ Copied aftermath-cli.jar to $INSTALL_DIR/aftermath-cli.jar"
else
    echo "⚠️ Warning: aftermath-cli.jar not found at $CLI_JAR. Build project first."
fi

cat << 'EOF' > "$INSTALL_DIR/aftermath"
#!/usr/bin/env bash
exec java -jar "$HOME/.aftermath/bin/aftermath-cli.jar" "$@"
EOF

chmod +x "$INSTALL_DIR/aftermath"
echo "✅ Created executable $INSTALL_DIR/aftermath"

if [[ ":$PATH:" != *":$INSTALL_DIR:"* ]]; then
    echo "export PATH=\"\$HOME/.aftermath/bin:\$PATH\"" >> "$HOME/.bashrc"
    echo "export PATH=\"\$HOME/.aftermath/bin:\$PATH\"" >> "$HOME/.zshrc" 2>/dev/null || true
    echo "✅ Added $INSTALL_DIR to PATH in ~/.bashrc and ~/.zshrc"
    echo "Please run 'source ~/.bashrc' or restart your terminal."
fi

echo "=========================================================================="
echo "🚀 INSTALLATION COMPLETE! Run 'aftermath status' or 'aftermath attach'"
echo "=========================================================================="
