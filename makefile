.PHONY: help

help:
	@echo "Commands:"
	@echo "  make build-api  Build the Docker image for investigation-api project."
	@echo "  make run-api    Run the Docker image of previously built investigation-api."

build-api:
	docker build -f ./projects/investigation-api/Dockerfile . -t corespin
run-api:
	docker run -p 3003:3003 -it corespin
