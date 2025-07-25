import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

public abstract class Financiamento implements Serializable {
    private static final long serialVersionUID = 1L;
    protected double valorImovel;
    protected int prazoFinanciamento;
    protected double taxaJurosAnual;
    private String tipo;

    public Financiamento(double valorImovel, int prazoFinanciamento, double taxaJurosAnual) {
        this.valorImovel = valorImovel;
        this.prazoFinanciamento = prazoFinanciamento;
        this.taxaJurosAnual = taxaJurosAnual;
        this.tipo = this.getClass().getSimpleName();
    }

    public double getValorImovel() {
        return valorImovel;
    }

    public int getPrazoFinanciamento() {
        return prazoFinanciamento;
    }

    public double getTaxaJurosAnual() {
        return taxaJurosAnual;
    }

    public String getTipo() {
        return tipo;
    }

    public double calcularPagamentoMensal() {
        double taxaMensal = this.taxaJurosAnual / 12 / 100;
        int numeroDePagamentos = this.prazoFinanciamento * 12;
        return (this.valorImovel * taxaMensal) / (1 - Math.pow(1 + taxaMensal, -numeroDePagamentos));
    }

    public double calcularTotalPagamento() {
        return calcularPagamentoMensal() * this.prazoFinanciamento * 12;
    }

    public abstract String toJson();

    public abstract String getResumo();

    @Override
    public abstract String toString();
}

class Casa extends Financiamento {
    private double areaConstruida;
    private double tamanhoTerreno;

    public Casa(double valorImovel, int prazoFinanciamento, double taxaJurosAnual, double areaConstruida, double tamanhoTerreno) {
        super(valorImovel, prazoFinanciamento, taxaJurosAnual);
        this.areaConstruida = areaConstruida;
        this.tamanhoTerreno = tamanhoTerreno;
    }

    public double calcularMensalComAumento(double acrescimo) throws AumentoMaiorDoQueJurosException {
        double mensal = calcularPagamentoMensal();
        double juros = mensal - (valorImovel / (prazoFinanciamento * 12));
        if (acrescimo > juros / 2) {
            throw new AumentoMaiorDoQueJurosException("Acréscimo de R$ " + acrescimo + " é maior que a metade dos juros da mensalidade (R$ " + (juros / 2) + ")");
        }
        return mensal + acrescimo;
    }

    public String getResumo() {
        return String.format("Casa (Área: %.1fm², Terreno: %.1fm²)", areaConstruida, tamanhoTerreno);
    }

    @Override
    public String toString() {
        try {
            double mensalComAumento = calcularMensalComAumento(80);
            return String.format(
                "Tipo: %s\n  - Valor do Imóvel: R$ %.2f\n  - Prazo: %d anos\n  - Taxa de Juros: %.2f%% a.a.\n  - Pagamento Mensal: R$ %.2f (com acréscimo: R$ %.2f)\n  - Total do Financiamento: R$ %.2f\n  - Área Construída: %.2f m²\n  - Tamanho do Terreno: %.2f m²",
                getTipo(), valorImovel, prazoFinanciamento, taxaJurosAnual,
                calcularPagamentoMensal(), mensalComAumento,
                calcularTotalPagamento(), areaConstruida, tamanhoTerreno);
        } catch (AumentoMaiorDoQueJurosException e) {
            return "[ERRO] " + e.getMessage();
        }
    }

    @Override
    public String toJson() {
        return String.format(
            "{\n  \"tipo\": \"%s\",\n  \"valorImovel\": %.2f,\n  \"prazoFinanciamento\": %d,\n  \"taxaJurosAnual\": %.2f,\n  \"areaConstruida\": %.2f,\n  \"tamanhoTerreno\": %.2f\n}",
            getTipo(), valorImovel, prazoFinanciamento, taxaJurosAnual, areaConstruida, tamanhoTerreno);
    }

    public Object getAreaConstruida() {

        throw new UnsupportedOperationException("Unimplemented method 'getAreaConstruida'");
    }

    public Object getTamanhoTerreno() {

        throw new UnsupportedOperationException("Unimplemented method 'getTamanhoTerreno'");
    }
}

// Custom exception class
class AumentoMaiorDoQueJurosException extends Exception {
    public AumentoMaiorDoQueJurosException(String message) {
        super(message);
    }
}

class Apartamento extends Financiamento {
    private int vagasGaragem;
    private int numeroAndar;

    public Apartamento(double valorImovel, int prazoFinanciamento, double taxaJurosAnual, int vagasGaragem, int numeroAndar) {
        super(valorImovel, prazoFinanciamento, taxaJurosAnual);
        this.vagasGaragem = vagasGaragem;
        this.numeroAndar = numeroAndar;
    }

    public String getResumo() {
        return String.format("Apartamento (Garagem: %d, Andar: %d)", vagasGaragem, numeroAndar);
    }

    @Override
    public String toString() {
        return String.format(
            "Tipo: %s\n  - Valor do Imóvel: R$ %.2f\n  - Prazo: %d anos\n  - Taxa de Juros: %.2f%% a.a.\n  - Pagamento Mensal: R$ %.2f\n  - Total do Financiamento: R$ %.2f\n  - Vagas na Garagem: %d\n  - Andar: %d",
            getTipo(), valorImovel, prazoFinanciamento, taxaJurosAnual,
            calcularPagamentoMensal(), calcularTotalPagamento(),
            vagasGaragem, numeroAndar);
    }

    @Override
    public String toJson() {
        return String.format(
            "{\n  \"tipo\": \"%s\",\n  \"valorImovel\": %.2f,\n  \"prazoFinanciamento\": %d,\n  \"taxaJurosAnual\": %.2f,\n  \"vagasGaragem\": %d,\n  \"numeroAndar\": %d\n}",
            getTipo(), valorImovel, prazoFinanciamento, taxaJurosAnual, vagasGaragem, numeroAndar);
    }

    public Object getVagasGaragem() {
        throw new UnsupportedOperationException("Unimplemented method 'getVagasGaragem'");
    }

    public Object getNumeroAndar() {
        throw new UnsupportedOperationException("Unimplemented method 'getNumeroAndar'");
    }
}

class Terreno extends Financiamento {
    private String tipoZona;

    public Terreno(double valorImovel, int prazoFinanciamento, double taxaJurosAnual, String tipoZona) {
        super(valorImovel, prazoFinanciamento, taxaJurosAnual);
        this.tipoZona = tipoZona;
    }

    public String getResumo() {
        return String.format("Terreno (Zona: %s)", tipoZona);
    }

    @Override
    public String toString() {
        return String.format(
            "Tipo: %s\n  - Valor do Imóvel: R$ %.2f\n  - Prazo: %d anos\n  - Taxa de Juros: %.2f%% a.a.\n  - Pagamento Mensal: R$ %.2f\n  - Total do Financiamento: R$ %.2f\n  - Tipo de Zona: %s",
            getTipo(), valorImovel, prazoFinanciamento, taxaJurosAnual,
            calcularPagamentoMensal(), calcularTotalPagamento(),
            tipoZona);
    }

    @Override
    public String toJson() {
        return String.format(
            "{\n  \"tipo\": \"%s\",\n  \"valorImovel\": %.2f,\n  \"prazoFinanciamento\": %d,\n  \"taxaJurosAnual\": %.2f,\n  \"tipoZona\": \"%s\"\n}",
            getTipo(), valorImovel, prazoFinanciamento, taxaJurosAnual, tipoZona);
    }

    public String getTipoZona() {
        throw new UnsupportedOperationException("Unimplemented method 'getTipoZona'");
    }
    public void salvarFinanciamentosSerializados(String nomeArquivo, List<Financiamento> financiamentos) {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nomeArquivo))) {
        oos.writeObject(financiamentos);
        System.out.println("Financiamentos serializados salvos em: " + nomeArquivo);
    } catch (IOException e) {
        System.err.println("Erro ao salvar serializado: " + e.getMessage());
    }
}
public void lerFinanciamentosSerializados(String nomeArquivo) {
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nomeArquivo))) {
        List<Financiamento> lidos = (List<Financiamento>) ois.readObject();
        System.out.println("\n--- Financiamentos lidos do arquivo serializado ---");
        for (Financiamento f : lidos) {
            System.out.println(f.getResumo());
        }
    } catch (IOException | ClassNotFoundException e) {
        System.err.println("Erro ao ler serializado: " + e.getMessage());
    }
}
}

