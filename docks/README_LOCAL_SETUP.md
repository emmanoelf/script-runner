# Creating database containers

```
cd docker
docker compose up
```
# Accessing pgAdmin4

Open http://localhost:16543/login default user and password for local development 
email: script.runner@leonet.com.br
password: root

## Connecting to local server on pgAdmin4

Access register server

![Access register server](image.png)

Give it a name

![Give it a name](image-1.png)

Configure local connection

![Configure local connection](image-2.png)

```
host name/address: 172.18.0.2
port: 5432
database: script_runner
user: postgres
password: root
```

# Configurando o docker no wsl para ser executado 

```
# Adiciona usuário no grupo do docker
sudo usermod -aG docker $USER

# Concede permissão ao usuário para executar o docker
sudo chmod 666 /var/run/docker.sock

# Exporta host para execução remota do docker
export DOCKER_HOST=unix:///var/run/docker.sock

# Adiciona variável no terminal
source ~/.bashrc
```