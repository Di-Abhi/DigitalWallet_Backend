#!/usr/bin/env bash
set -euo pipefail

# Usage:
#   sudo bash setup-ec2.sh /opt/loyalty-service
# If path is omitted, /opt/loyalty-service is used.

DEPLOY_PATH="${1:-/opt/loyalty-service}"
SSH_USER="${SUDO_USER:-ubuntu}"

if [[ "$EUID" -ne 0 ]]; then
  echo "Run this script with sudo/root privileges."
  exit 1
fi

echo "[1/5] Installing Docker and Compose plugin..."
apt-get update -y
apt-get install -y docker.io docker-compose-plugin
systemctl enable docker
systemctl start docker

echo "[2/5] Adding ${SSH_USER} to docker group..."
usermod -aG docker "${SSH_USER}" || true

echo "[3/5] Creating deploy directory at ${DEPLOY_PATH}..."
mkdir -p "${DEPLOY_PATH}"
chown -R "${SSH_USER}:${SSH_USER}" "${DEPLOY_PATH}"

echo "[4/5] Ensuring SSH directory permissions..."
install -d -m 700 -o "${SSH_USER}" -g "${SSH_USER}" "/home/${SSH_USER}/.ssh"
touch "/home/${SSH_USER}/.ssh/authorized_keys"
chown "${SSH_USER}:${SSH_USER}" "/home/${SSH_USER}/.ssh/authorized_keys"
chmod 600 "/home/${SSH_USER}/.ssh/authorized_keys"

echo "[5/5] Done."
echo "Next steps:"
echo "1) Add your GitHub Actions public key to /home/${SSH_USER}/.ssh/authorized_keys"
echo "2) Configure GitHub repo secrets (SSH_HOST, SSH_USERNAME, SSH_PRIVATE_KEY, DEPLOY_PATH, PROD_ENV_FILE, REGISTRY_USERNAME, REGISTRY_PASSWORD)"
echo "3) Push to main branch to trigger deployment"
