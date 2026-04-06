## GitHub Actions + EC2 Deployment

This project already includes end-to-end deployment via:

- workflow: `.github/workflows/ci-cd.yml`
- production compose file: `deploy/docker-compose.prod.yml`

On push to `main`, the workflow:

1. Runs `mvn clean verify` for each service.
2. Builds and pushes Docker images to GHCR.
3. SSHes into EC2 and updates the running stack with `docker compose`.

## 1) Prepare EC2 (one-time)

SSH into your Ubuntu EC2 host and run:

```bash
cd /tmp
curl -fsSL https://raw.githubusercontent.com/<your-user>/<your-repo>/main/deploy/setup-ec2.sh -o setup-ec2.sh
sudo bash setup-ec2.sh /opt/loyalty-service
```

If you do not want to download it, copy and run `deploy/setup-ec2.sh` manually.

## 2) Configure GitHub Actions secrets

Add these in `GitHub Repo -> Settings -> Secrets and variables -> Actions`:

- `SSH_HOST`: EC2 public IP or DNS
- `SSH_PORT`: usually `22`
- `SSH_USERNAME`: SSH user on EC2 (for Ubuntu AMI, usually `ubuntu`)
- `SSH_PRIVATE_KEY`: private key content used by Actions for SSH
- `DEPLOY_PATH`: e.g. `/opt/loyalty-service`
- `PROD_ENV_FILE`: full content of your production env file
- `REGISTRY_USERNAME`: GHCR username (usually your GitHub username)
- `REGISTRY_PASSWORD`: GHCR token/password with package read permission

## 3) Set production environment values

Use `deploy/.env.production.example` as template for `PROD_ENV_FILE`.

Important:

- Set strong secrets (`JWT_SECRET`, DB passwords, mail/cloud keys, payment keys).
- Keep service hostnames as defined in compose (for example `auth-db`, `kafka`, `redis`).

## 4) Trigger deployment

Push to `main`:

```bash
git push origin main
```

Then verify in GitHub Actions logs that `deploy` job ran successfully.

## 5) Verify on EC2

```bash
cd /opt/loyalty-service
docker compose -f docker-compose.prod.yml ps
docker compose -f docker-compose.prod.yml logs --tail=100 api-gateway
```

If required, open these ports in EC2 Security Group:

- `22` for SSH (restrict to your IP)
- `8080` for API gateway (or put Nginx in front and expose `80/443`)
