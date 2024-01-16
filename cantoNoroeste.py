
    def canto_noroeste(cost_matrix, supply, demand):
    num_suppliers = len(supply)
    num_customers = len(demand)

    allocation = [[0] * num_customers for _ in range(num_suppliers)]
    i, j = 0, 0

    while i < num_suppliers and j < num_customers:
        quantity = min(supply[i], demand[j])
        allocation[i][j] = quantity

        supply[i] -= quantity
        demand[j] -= quantity

        if supply[i] == 0:
            i += 1
        if demand[j] == 0:
            j += 1

    return allocation

cost_matrix = [
    [2, 4, 3],
    [5, 2, 1],
    [6, 3, 4]
]

supply = [20, 30, 50]
demand = [30, 40, 30]

result = canto_noroeste(cost_matrix, supply, demand)

for i in range(len(result)):
    for j in range(len(result[0])):
        print(f"Transportar {result[i][j]} unidades do fornecedor {i + 1} para o cliente {j + 1}")
