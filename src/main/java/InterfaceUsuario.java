import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class InterfaceUsuario {
    private Scanner scanner;
    private List<Financiamento> financiamentosAtivos;
    private List<Financiamento> todosOsExemplos;

    public InterfaceUsuario(List<Financiamento> exemplos) {
        this.scanner = new Scanner(System.in);
        this.todosOsExemplos = exemplos;
        this.financiamentosAtivos = new ArrayList<>(exemplos);
    }

    public void iniciar() {
        int opcao;
        do {
            exibirMenuPrincipal();
            opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    mostrarSubMenu("Casa");
                    break;
                case 2:
                    mostrarSubMenu("Apartamento");
                    break;
                case 3:
                    mostrarSubMenu("Terreno");
                    break;
                case 4:
                    somarFinanciamentos();
                    break;
                case 5:
                    reiniciarFinanciamentos();
                    break;
                case 6:
                    salvarFinanciamentosEmTxt("financiamentos.txt");
                    salvarComoDat("financiamentos.dat");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }

        } while (opcao != 6);
        System.out.println("Programa encerrado.");
        scanner.close();
    }

    private void exibirMenuPrincipal() {
        System.out.println("\n===== FINANCIAMENTOS =====");
        System.out.println("1. Casas");
        System.out.println("2. Apartamentos");
        System.out.println("3. Terrenos");
        System.out.println("--------------------------");
        System.out.println("4. Somar financiamentos");
        System.out.println("5. Reiniciar (restaurar todos os exemplos)");
        System.out.println("6. Salvar e Sair");
        System.out.print("Escolha uma opção: ");
    }

    private int lerOpcao() {
        while (!scanner.hasNextInt()) {
            System.out.println("Opção inválida. Por favor, digite um número.");
            scanner.next();
        }
        int opcao = scanner.nextInt();
        scanner.nextLine();
        return opcao;
    }

    private void mostrarSubMenu(String tipo) {
        List<Financiamento> filtrados = financiamentosAtivos.stream()
                .filter(f -> f.getTipo().equalsIgnoreCase(tipo))
                .collect(Collectors.toList());

        if (filtrados.isEmpty()) {
            System.out.printf("\nNão há financiamentos de '%s' para mostrar.\n", tipo);
            return;
        }

        int escolha;
        do {
            System.out.printf("\n--- Financiamentos de %s ---\n", tipo);
            for (int i = 0; i < filtrados.size(); i++) {
                System.out.printf("%d. %s (Valor: R$ %.2f)\n", i + 1, filtrados.get(i).getResumo(), filtrados.get(i).getValorImovel());
            }
            System.out.println("--------------------------");
            System.out.printf("%d. Voltar\n", filtrados.size() + 1);
            System.out.print("Escolha uma opção para ver os detalhes ou voltar: ");

            escolha = lerOpcao();

            if (escolha > 0 && escolha <= filtrados.size()) {
                Financiamento selecionado = filtrados.get(escolha - 1);
                System.out.println("\n--- Detalhes do Financiamento ---");
                System.out.println(selecionado.toString());
            } else if (escolha != filtrados.size() + 1) {
                System.out.println("Opção inválida.");
            }
        } while (escolha != filtrados.size() + 1);
    }

    private void somarFinanciamentos() {
        if (financiamentosAtivos.size() < 2) {
            System.out.println("\nÉ necessário ter pelo menos 2 financiamentos ativos para somar.");
            return;
        }

        List<Financiamento> paraSomar = new ArrayList<>();

        while (true) {
            System.out.println("\n--- Somar Financiamentos ---");
            System.out.println("Financiamentos disponíveis para adicionar à soma:");
            for (int i = 0; i < financiamentosAtivos.size(); i++) {
                boolean jaSelecionado = paraSomar.contains(financiamentosAtivos.get(i));
                System.out.printf("%d. %s (Valor: R$ %.2f) %s\n",
                        i + 1,
                        financiamentosAtivos.get(i).getResumo(),
                        financiamentosAtivos.get(i).getValorImovel(),
                        (jaSelecionado ? "[SELECIONADO]" : "")
                );
            }
            System.out.println("--------------------------------");
            System.out.print("Escolha um financiamento para adicionar à soma: ");

            int escolha = lerOpcao();
            int idx = escolha - 1;

            if (idx < 0 || idx >= financiamentosAtivos.size()) {
                System.out.println("Opção inválida.");
                continue;
            }

            Financiamento selecionado = financiamentosAtivos.get(idx);

            if (paraSomar.contains(selecionado)) {
                System.out.println("\n[ERRO] Não pode selecionar a mesma opção. Por favor, escolha outro financiamento.");
                continue;
            }

            paraSomar.add(selecionado);
            System.out.printf("\n-> '%s' adicionado à soma.\n", selecionado.getTipo());

            if (paraSomar.size() == financiamentosAtivos.size()) {
                System.out.println("\nTodos os financiamentos foram adicionados à soma.");
                break;
            }

            System.out.print("Deseja somar mais algum? (s/n): ");
            String resposta = scanner.nextLine();
            if (!resposta.equalsIgnoreCase("s")) {
                break;
            }
        }

        if (paraSomar.size() < 2) {
            System.out.println("\nSoma cancelada. É preciso selecionar pelo menos dois financiamentos.");
            return;
        }

        double somaValorImoveis = 0;
        double somaValorTotalFinanciado = 0;

        for (Financiamento f : paraSomar) {
            somaValorImoveis += f.getValorImovel();
            somaValorTotalFinanciado += f.calcularTotalPagamento();
        }

        System.out.println("\n========= RESULTADO FINAL DA SOMA =========");
        System.out.printf("Itens somados: %d\n", paraSomar.size());
        System.out.printf("Soma do valor dos imóveis: R$ %.2f\n", somaValorImoveis);
        System.out.printf("Soma do valor total dos financiamentos: R$ %.2f\n", somaValorTotalFinanciado);
        System.out.println("===========================================");
    }

    private void reiniciarFinanciamentos() {
        this.financiamentosAtivos = new ArrayList<>(this.todosOsExemplos);
        System.out.println("\nLista de financiamentos restaurada para os exemplos iniciais.");
    }

    public void salvarFinanciamentosEmTxt(String nomeArquivo) {
        try (FileWriter writer = new FileWriter(nomeArquivo)) {
            for (Financiamento f : financiamentosAtivos) {
                writer.write(f.toString().replace("\n", " | ") + "\n");
            }
            System.out.println("\n+-------------------------------------------------+");
            System.out.printf("| Dados salvos com sucesso em '%s' |\n", nomeArquivo);
            System.out.println("+-------------------------------------------------+\n");
        } catch (IOException e) {
            System.err.println("Erro ao salvar no arquivo de texto: " + e.getMessage());
        }
    }

    public void lerFinanciamentosDeTxt(String nomeArquivo) {
        System.out.println("\n+----------------- DADOS LIDOS --------------------+");
        try (Scanner leitor = new Scanner(new File(nomeArquivo))) {
            while (leitor.hasNextLine()) {
                System.out.println(leitor.nextLine());
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo de texto: " + e.getMessage());
        }
        System.out.println("+-------------------------------------------------+\n");
    }

    public void salvarComoDat(String nomeArquivo) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(nomeArquivo))) {
            out.writeObject(financiamentosAtivos);
            System.out.printf("Arquivo binário salvo com sucesso em '%s'\n", nomeArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao salvar como .dat: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void lerDeDat(String nomeArquivo) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(nomeArquivo))) {
            List<Financiamento> carregados = (List<Financiamento>) in.readObject();
            System.out.println("\n--- Financiamentos carregados do arquivo .dat ---");
            for (Financiamento f : carregados) {
                System.out.println(f.getResumo() + " | R$ " + f.getValorImovel());
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao ler o .dat: " + e.getMessage());
        }
    }
}
