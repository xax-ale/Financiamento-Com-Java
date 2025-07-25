import java.util.ArrayList;
import java.util.List;

public class Main { public static void main(String[] args) {
        List<Financiamento> exemplos = new ArrayList<>();
        exemplos.add(new Casa(350000, 30, 8.5, 120.0, 200.0));
        exemplos.add(new Casa(520000, 25, 9.0, 150.0, 300.0));
        exemplos.add(new Apartamento(280000, 20, 8.0, 1, 4));
        exemplos.add(new Apartamento(410000, 30, 8.2, 2, 7));
        exemplos.add(new Terreno(150000, 15, 10.0, "Residencial"));

        Casa casaTeste = new Casa(100000, 30, 3.0, 60.0, 100.0);
        exemplos.add(casaTeste);

        try {
            double novaParcela = casaTeste.calcularMensalComAumento(8880);
            System.out.printf("Parcela com acréscimo: R$ %.2f\n", novaParcela);
        } catch (AumentoMaiorDoQueJurosException e) {
            System.out.println("[EXCEÇÃO] " + e.getMessage());
        }

        InterfaceUsuario ui = new InterfaceUsuario(exemplos);
        ui.iniciar();
        ui.salvarFinanciamentosEmTxt("financiamentos.txt");
        ui.lerFinanciamentosDeTxt("financiamentos.txt");
        ui.salvarComoDat("financiamentos.dat");
        ui.lerDeDat("financiamentos.dat");
    }
}

class AumentoMaiorDoQueJurosException extends Exception {
    public AumentoMaiorDoQueJurosException(String mensagem) {
        super(mensagem);
    }
}

  