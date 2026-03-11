set -e

echo "MarketFlow — Setup"
echo "─────────────────────────────────────────"

echo "Verificando dependências..."
for cmd in docker kind kubectl helm; do
  if ! command -v $cmd &> /dev/null; then
    echo "❌ '$cmd' não encontrado. Instale antes de continuar."
    exit 1
  fi
done
echo "Dependências OK"

echo ""
echo "🔧 Criando cluster Kind.."
if kind get clusters | grep -q "marketflow"; then
  echo "Cluster 'marketflow' já existe. Pulando criação."
else
  kind create cluster --config kind-config.yaml
  echo "Cluster criado"
fi

echo ""
echo "Instalando nginx-ingress..."
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
echo "Aguardando nginx-ingress ficar pronto..."
sleep 10
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=120s
echo "nginx-ingress pronto"

echo ""
echo "Fazendo build da imagem user-service..."
cd ../user-service
./mvnw package -DskipTests
docker build -f ../k8s-helm/Dockerfile.user-service -t marketflow/user-service:latest .
kind load docker-image marketflow/user-service:latest --name marketflow
cd ../k8s-helm
echo "Imagem carregada no cluster"

echo ""
echo "⎈  Instalando Helm chart..."
helm upgrade --install marketflow ./helm/marketflow \
  --namespace marketflow \
  --create-namespace \
  --wait
echo "Helm chart instalado"

echo ""
echo "Adicionando entry no /etc/hosts..."
if ! grep -q "user-service.marketflow.local" /etc/hosts; then
  echo "127.0.0.1 user-service.marketflow.local" | sudo tee -a /etc/hosts
  echo "Entry adicionada"
else
  echo "Entry já existe no /etc/hosts"
fi

echo ""
echo "─────────────────────────────────────────"
echo "MarketFlow rodando!"
echo ""
echo "Endpoints:"
echo "   user-service  → http://user-service.marketflow.local"
echo "   RabbitMQ UI   → http://localhost:15672"
echo ""
echo "Comandos úteis:"
echo "   kubectl get pods -n marketflow"
echo "   kubectl logs -n marketflow deploy/user-service -f"
echo "   helm status marketflow -n marketflow"
echo "─────────────────────────────────────────"