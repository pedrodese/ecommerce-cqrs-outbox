set -e

echo "Removendo MarketFlow..."

kind delete cluster --name marketflow

echo "Cluster removido"