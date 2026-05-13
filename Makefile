# Name Files
COMPOSE_PROD=docker-compose.prod.yaml
COMPOSE_BASE=docker-compose.yaml
COMPOSE_DEV=docker-compose.dev.yaml
ENV_DEV=.env.dev
ENV_PROD=.env.prod

# CMD prod
prod-build:
	docker compose --env-file $(ENV_PROD) -f $(COMPOSE_PROD) up --build -d --remove-orphans
prod-up:
	docker compose --env-file $(ENV_PROD) -f $(COMPOSE_PROD) up -d

prod-down:
	docker compose --env-file $(ENV_PROD) -f $(COMPOSE_PROD) down --timeout 5

prod-down-vol:
	docker compose --env-file $(ENV_PROD) -f $(COMPOSE_PROD) down -v --remove-orphans --timeout 5

prod-logs:
	docker compose -f $(COMPOSE_PROD) logs -f

prod-restart:
	docker compose --env-file $(ENV_PROD) -f $(COMPOSE_PROD) restart

# CMD dev
dev-build:
	docker compose --env-file $(ENV_DEV) -f $(COMPOSE_BASE) -f $(COMPOSE_DEV) up --build -d --remove-orphans

dev-up:
	docker compose --env-file $(ENV_DEV) -f $(COMPOSE_BASE) -f $(COMPOSE_DEV) up -d

dev-down:
	docker compose --env-file $(ENV_DEV) -f $(COMPOSE_BASE) -f $(COMPOSE_DEV) down --timeout 5

dev-down-vol:
	docker compose --env-file $(ENV_DEV) -f $(COMPOSE_BASE) -f $(COMPOSE_DEV) down -v --timeout 5 --remove-orphans

dev-logs:
	docker compose --env-file $(ENV_DEV) -f $(COMPOSE_BASE) -f $(COMPOSE_DEV) logs -f

dev-restart:
	docker compose --env-file $(ENV_DEV) -f $(COMPOSE_BASE) -f $(COMPOSE_DEV) restart

clean-dev:
	docker compose --env-file $(ENV_DEV) -f $(COMPOSE_BASE) -f $(COMPOSE_DEV) down -v --remove-orphans --timeout 5

clean-prod:
	docker compose -f $(COMPOSE_PROD) down -v --remove-orphans --timeout 5

# Containers status
status:
	docker ps -a