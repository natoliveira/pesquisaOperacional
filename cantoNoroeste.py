def canto_noroeste(cost_matrix, supply, demand):
    # Obtém o número de fornecedores e clientes
    num_suppliers = len(supply)
    num_customers = len(demand)

    # Inicializa uma matriz de alocação com zeros
    allocation = [[0] * num_customers for _ in range(num_suppliers)]
    
    # Inicializa os índices para percorrer a matriz de custo
    i, j = 0, 0

    # Inicia o método do Canto Noroeste para alocação
    while i < num_suppliers and j < num_customers:
        # Determina a quantidade a ser alocada (mínimo entre oferta do fornecedor e demanda do cliente)
        quantity = min(supply[i], demand[j])
        
        # Aloca a quantidade na matriz de alocação
        allocation[i][j] = quantity

        # Atualiza a oferta e demanda após alocar
        supply[i] -= quantity
        demand[j] -= quantity

        # Avança para o próximo fornecedor se a oferta for totalmente atendida
        if supply[i] == 0:
            i += 1
        
        # Avança para o próximo cliente se a demanda for totalmente atendida
        if demand[j] == 0:
            j += 1

    # Retorna a matriz de alocação resultante
    return allocation

# Exemplo de matriz de custo, oferta e demanda
cost_matrix = [
    [2, 4, 3],
    [5, 2, 1],
    [6, 3, 4]
]

supply = [20, 30, 50]
demand = [30, 40, 30]

# Chama a função e obter a matriz de alocação resultante
result = canto_noroeste(cost_matrix, supply, demand)

# Imprimir os resultados
for i in range(len(result)):
    for j in range(len(result[0])):
        print(f"Transportar {result[i][j]} unidades do fornecedor {i + 1} para o cliente {j + 1}")
