import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CalculadoraDosimetria extends JFrame {

    // Campos de entrada principais
    private JTextField entradaPena;
    private JTextField entradaCircNeg;
    private JTextField entradaCircPos;
    private JTextField entradaAgravantes;
    private JTextField entradaAtenuantes;
    private JComboBox<String> comboFraBase;
    private JComboBox<String> comboFraIntermediaria;
    private JTextField entradaAumentos;
    private JTextField entradaDiminuicoes;
    private JLabel labelResultado;

    // Aba de concursos
    private JTextField entradaConcursoTipo;
    private ArrayList<JTextField> listaPenas;
    private JTextField entradaFracaoExasperacao;
    private JLabel labelSoma;
    private JLabel labelExasperacao;

    public CalculadoraDosimetria() {
        setTitle("Calculadora de Dosimetria Penal");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane abas = new JTabbedPane();

        // Aba principal
        JPanel abaPrincipal = new JPanel();
        abaPrincipal.setLayout(new BorderLayout());

        // Cria o painel com os campos
        JPanel painelCampos = new JPanel(new GridLayout(11, 2, 5, 5));

        // Adiciona os campos no painelCampos
        painelCampos.add(new JLabel("Pena mínima (ex: 2a3m15d20dm):"));
        entradaPena = new JTextField();
        painelCampos.add(entradaPena);

        entradaPena.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validarFormatoPena(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validarFormatoPena(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validarFormatoPena(); }
});

        painelCampos.add(new JLabel("Circunstâncias negativas:"));
        entradaCircNeg = new JTextField(5);
        painelCampos.add(entradaCircNeg);

        painelCampos.add(new JLabel("Circunstâncias positivas:"));
        entradaCircPos = new JTextField(5);
        painelCampos.add(entradaCircPos);

        painelCampos.add(new JLabel("Fração de Aumento (Pena-base):"));
        comboFraBase = new JComboBox<>(new String[]{"1/6", "1/8"});
        painelCampos.add(comboFraBase);
        
        painelCampos.add(new JLabel("Agravantes:"));
        entradaAgravantes = new JTextField(5);
        painelCampos.add(entradaAgravantes);

        painelCampos.add(new JLabel("Atenuantes:"));
        entradaAtenuantes = new JTextField(5);
        painelCampos.add(entradaAtenuantes);

        painelCampos.add(new JLabel("Fração de Aumento (Pena Intermediária):"));
        comboFraIntermediaria = new JComboBox<>(new String[]{"1/6", "1/8"});
        painelCampos.add(comboFraIntermediaria);

        painelCampos.add(new JLabel("Frações de aumento (ex: 1/2;1/3):"));
        entradaAumentos = new JTextField();
        painelCampos.add(entradaAumentos);

        painelCampos.add(new JLabel("Frações de diminuição (ex: 1/5;1/4):"));
        entradaDiminuicoes = new JTextField();
        painelCampos.add(entradaDiminuicoes);

        JButton botaoCalcular = new JButton("Calcular");
        painelCampos.add(botaoCalcular);

        JButton botaoAjuda = new JButton("Ajuda");
        painelCampos.add(botaoAjuda);

        // Adiciona os painéis no abaPrincipal
        abaPrincipal.add(painelCampos, BorderLayout.CENTER);

        labelResultado = new JLabel("");
        labelResultado.setVerticalAlignment(SwingConstants.TOP);
        abaPrincipal.add(labelResultado, BorderLayout.SOUTH);

        // Adiciona a aba ao TabbedPane
        abas.addTab("Dosimetria", abaPrincipal);

        // Ações dos botões
        botaoCalcular.addActionListener(e -> calcular());
        botaoAjuda.addActionListener(e -> mostrarAjuda());

        getContentPane().add(abas);

        // Validação em tempo real do campo de pena
        entradaPena.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                validarFormatoPena();
    }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                validarFormatoPena();
    }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    validarFormatoPena();
    }
});

        // Aba de concursos
        JPanel abaConcurso = new JPanel(new GridBagLayout());
        GridBagConstraints gbcConcurso = new GridBagConstraints();
        gbcConcurso.insets = new Insets(5, 5, 5, 5);
        gbcConcurso.anchor = GridBagConstraints.WEST;
        gbcConcurso.fill = GridBagConstraints.HORIZONTAL;
        gbcConcurso.weightx = 1.0;

        int linhaConcurso = 0;

        gbcConcurso.gridx = 0;
        gbcConcurso.gridy = linhaConcurso;
        abaConcurso.add(new JLabel("Concurso (M, F, C):"), gbcConcurso);

        entradaConcursoTipo = new JTextField("M");
        gbcConcurso.gridx = 1;
        abaConcurso.add(entradaConcursoTipo, gbcConcurso);

        linhaConcurso++;
        gbcConcurso.gridx = 0;
        gbcConcurso.gridy = linhaConcurso;
        abaConcurso.add(new JLabel("Pena 1:"), gbcConcurso);

        listaPenas = new ArrayList<>();
        JTextField primeiraPena = new JTextField();
        listaPenas.add(primeiraPena);
        gbcConcurso.gridx = 1;
        abaConcurso.add(primeiraPena, gbcConcurso);

        // Validação em tempo real da primeira pena
        primeiraPena.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                CalculadoraDosimetria.this.validarFormatoPenaConcurso(primeiraPena);
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                CalculadoraDosimetria.this.validarFormatoPenaConcurso(primeiraPena);
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                CalculadoraDosimetria.this.validarFormatoPenaConcurso(primeiraPena);
            }
        });     

        linhaConcurso++;
        gbcConcurso.gridx = 0;
        gbcConcurso.gridy = linhaConcurso;
        gbcConcurso.gridwidth = 2;
        JButton botaoAdicionarPena = new JButton("Adicionar Pena");
        abaConcurso.add(botaoAdicionarPena, gbcConcurso);

        linhaConcurso++;
        gbcConcurso.gridx = 0;
        gbcConcurso.gridy = linhaConcurso;
        gbcConcurso.gridwidth = 1;
        abaConcurso.add(new JLabel("Fração de Exasperação (ex: 1/6):"), gbcConcurso);

        entradaFracaoExasperacao = new JTextField();
        gbcConcurso.gridx = 1;
        abaConcurso.add(entradaFracaoExasperacao, gbcConcurso);

        linhaConcurso++;
        gbcConcurso.gridx = 0;
        gbcConcurso.gridy = linhaConcurso;
        gbcConcurso.gridwidth = 2;
        JButton botaoCalcularConcurso = new JButton("Calcular Concurso");
        abaConcurso.add(botaoCalcularConcurso, gbcConcurso);

        linhaConcurso++;
        gbcConcurso.gridy = linhaConcurso;
        labelSoma = new JLabel("");
        abaConcurso.add(labelSoma, gbcConcurso);

        linhaConcurso++;
        gbcConcurso.gridy = linhaConcurso;
        labelExasperacao = new JLabel("");
        abaConcurso.add(labelExasperacao, gbcConcurso);

        abas.addTab("Concurso", abaConcurso);

        // Ações dos botões
        botaoAdicionarPena.addActionListener(e -> adicionarCampoPena(abaConcurso));
        botaoCalcularConcurso.addActionListener(e -> calcularConcurso());
        botaoAjuda.addActionListener(e -> mostrarAjuda());
        
        getContentPane().add(abas);
   
    }

    // Métodos de cálculo e ajuda (ainda serão implementados)
    private void calcular() {
        try {
            String entradaPenaStr = entradaPena.getText().isEmpty() ? "0a0m0d0dm" : entradaPena.getText();
            Pena penaMin = Pena.parse(entradaPenaStr);
        

            int diasMultaMin = penaMin.getDiasMulta();
            int circunstanciasNeg = parseInteiro(entradaCircNeg.getText());
            int circunstanciasPos = parseInteiro(entradaCircPos.getText());
            int agravantes = parseInteiro(entradaAgravantes.getText());
            int atenuantes = parseInteiro(entradaAtenuantes.getText());

            double fracaoBase = parseFracao((String) comboFraBase.getSelectedItem());
            double fracaoIntermediaria = parseFracao((String) comboFraIntermediaria.getSelectedItem());

            int diasMin = penaMin.converterParaDias();

            // Pena-base (PPL)
            int diasBase = diasMin + (int) Math.round(diasMin * circunstanciasNeg * fracaoBase);
            diasBase -= (int) Math.round(diasMin * circunstanciasPos * fracaoBase);
            diasBase = Math.max(diasBase, diasMin);

            // Pena-base dias-multa
            int diasMultaBase = diasMultaMin + (int) Math.round(diasMultaMin * circunstanciasNeg * fracaoBase);
            diasMultaBase -= (int) Math.round(diasMultaMin * circunstanciasPos * fracaoBase);
            diasMultaBase = Math.max(diasMultaBase, diasMultaMin);

            // Pena intermediária
            int diasIntermediaria = diasBase;
            int diasMultaIntermediaria = diasMultaBase;

            if (agravantes > atenuantes) {
                int restante = agravantes - atenuantes;
                diasIntermediaria += (int) Math.round(diasMin * restante * fracaoIntermediaria);
                diasMultaIntermediaria += (int) Math.round(diasMultaMin * restante * fracaoIntermediaria);
            } else if (atenuantes > agravantes) {
                int restante = atenuantes - agravantes;
                diasIntermediaria -= (int) Math.round(diasMin * restante * fracaoIntermediaria);
                diasMultaIntermediaria -= (int) Math.round(diasMultaMin * restante * fracaoIntermediaria);
            }

            diasIntermediaria = Math.max(diasIntermediaria, diasMin);
            diasMultaIntermediaria = Math.max(diasMultaIntermediaria, diasMultaMin);

            // Pena definitiva
            int diasDefinitiva = diasIntermediaria;
            int diasMultaDefinitiva = diasMultaIntermediaria;

            String[] aumentos = entradaAumentos.getText().split(";");
            for (String aumento : aumentos) {
                if (!aumento.trim().isEmpty()) {
                    double fra = parseFracao(aumento.trim());
                    diasDefinitiva += (int) Math.round(diasIntermediaria * fra);
                    diasMultaDefinitiva += (int) Math.round(diasMultaIntermediaria * fra);
                }
            }

            String[] diminuicoes = entradaDiminuicoes.getText().split(";");
                for (String dimin : diminuicoes) {
                if (!dimin.trim().isEmpty()) {
                    double fra = parseFracao(dimin.trim());
                    diasDefinitiva -= (int) Math.round(diasDefinitiva * fra);
                    diasMultaDefinitiva -= (int) Math.round(diasMultaDefinitiva * fra);
                }
            }

            diasDefinitiva = Math.max(diasDefinitiva, diasMin);
            diasMultaDefinitiva = Math.max(diasMultaDefinitiva, 0);

            // Exibir o resultado
            String resultado = String.format(
                "<html>Pena-base: %s, Dias-multa: %ddm<br>" +
                "Pena intermediária: %s, Dias-multa: %ddm<br>" +
                "Pena definitiva: %s, Dias-multa: %ddm</html>",
                Pena.formatarDias(diasBase), diasMultaBase,
                Pena.formatarDias(diasIntermediaria), diasMultaIntermediaria,
                Pena.formatarDias(diasDefinitiva), diasMultaDefinitiva
            );

            labelResultado.setText(resultado);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de entrada", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }    
    private int parseInteiro(String texto) {
        if (texto == null || texto.isEmpty()) return 0;
        return Integer.parseInt(texto.trim());
    }

    private double parseFracao(String texto) throws IllegalArgumentException {
        String[] partes = texto.split("/");
        if (partes.length != 2) throw new IllegalArgumentException("Fração inválida: " + texto);
        double numerador = Double.parseDouble(partes[0].trim());
        double denominador = Double.parseDouble(partes[1].trim());
        if (denominador == 0) throw new IllegalArgumentException("Denominador da fração não pode ser zero.");
        return numerador / denominador;
    }
    private void validarFormatoPenaConcurso(JTextField campo) {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            campo.setBackground(Color.WHITE);
            campo.setToolTipText(null);
            return;
        }

        try {
            Pena.parse(texto);
            campo.setBackground(new Color(200, 255, 200)); // Verde claro indica válido
            campo.setToolTipText("Formato válido.");
        } catch (IllegalArgumentException ex) {
            campo.setBackground(new Color(255, 200, 200)); // Vermelho claro indica erro
            campo.setToolTipText("Formato inválido! Use algo como 2a3m15d20dm.");
        }
    }

    private void calcularConcurso() {
        try {
            String tipoConcurso = entradaConcursoTipo.getText().trim().toUpperCase();
            if (!(tipoConcurso.equals("M") || tipoConcurso.equals("F") || tipoConcurso.equals("C"))) {
                throw new IllegalArgumentException("Tipo de concurso inválido. Use M, F ou C.");
            }

        ArrayList<Pena> penas = new ArrayList<>();
        for (JTextField campo : listaPenas) {
            String texto = campo.getText().trim();
            if (!texto.isEmpty()) {
                penas.add(Pena.parse(texto));
            }
        }

        if (penas.isEmpty()) {
            throw new IllegalArgumentException("Insira ao menos uma pena.");
        }

        int totalDias = 0;
        int totalDiasMulta = 0;

        for (Pena p : penas) {
            totalDias += p.converterParaDias();
            totalDiasMulta += p.getDiasMulta();
        }

        if (tipoConcurso.equals("M")) {
            labelSoma.setText(String.format("Soma: %s, Dias-multa: %ddm", Pena.formatarDias(totalDias), totalDiasMulta));
            labelExasperacao.setText("");
        } else {
            // Concurso Formal ou Crime Continuado
            Pena maiorPena = penas.get(0);
            for (Pena p : penas) {
                if (p.converterParaDias() > maiorPena.converterParaDias()) {
                    maiorPena = p;
                }
            }

            int diasMaiorPena = maiorPena.converterParaDias();
            int diasMultaMaiorPena = maiorPena.getDiasMulta();

            String fracaoExasperacao = entradaFracaoExasperacao.getText().trim();
            if (fracaoExasperacao.isEmpty()) {
                throw new IllegalArgumentException("Informe a fração de exasperação.");
            }

            double fra = parseFracao(fracaoExasperacao);

            int aumentoDias = (int) Math.round(diasMaiorPena * fra);
            int aumentoDiasMulta = (int) Math.round(diasMultaMaiorPena * fra);

            int diasExasperados = diasMaiorPena + aumentoDias;
            int diasMultaExasperados = diasMultaMaiorPena + aumentoDiasMulta;

            labelExasperacao.setText(String.format("Pena exasperada: %s, Dias-multa: %ddm", Pena.formatarDias(diasExasperados), diasMultaExasperados));
            labelSoma.setText("");
        }

    } catch (IllegalArgumentException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de entrada", JOptionPane.ERROR_MESSAGE);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}

    private void validarFormatoPena() {
    String texto = entradaPena.getText().trim();
    if (texto.isEmpty()) {
        entradaPena.setBackground(Color.WHITE);
        entradaPena.setToolTipText(null);
        return;
    }

    try {
        Pena.parse(texto);
        entradaPena.setBackground(new Color(200, 255, 200)); // Verde claro indica válido
        entradaPena.setToolTipText("Formato válido.");
    } catch (IllegalArgumentException ex) {
        entradaPena.setBackground(new Color(255, 200, 200)); // Vermelho claro indica erro
        entradaPena.setToolTipText("Formato inválido! Use algo como 2a3m15d20dm.");
    }
}

    private void mostrarAjuda() {
        String ajudaTexto = """
        
CALCULADORA DE DOSIMETRIA

Formato de Entrada da Pena Mínima: use o formato 2a3m15d20dm para indicar 2 anos, 3 meses, 15 dias 
e 20 dias-multa. Não é necessário preencher todos os campos, sendo aceito, por exemplo: 2a10dm para pena 
mínima de 2 anos, com 10 dias-multa.

Campos Adicionais:

- Circunstâncias negativas/positivas: são as circunstâncias judiciais, devendo ser inserida a 
quantidade. Por exemplo, se a culpabilidade e os motivos forem desfavoráveis, insira: 2 em 
negativas. O mesmo ocorre para as circunstâncias positivas (comportamento da vítima).

- Agravantes/Atenuantes: o modo de aplicação é o mesmo das circunstâncias judiciais, devendo 
ser apontada a quantidade. No caso de valores iguais, a calculadora promoverá a compensação. 
Se, por exemplo, houver multirreincidência, deve ser inserida a quantidade de vezes que você 
pretende considerá-la, para que a compensação ocorra apenas de forma parcial.

- Frações de aumento/diminuição: Use frações separadas por ponto e vírgula, sem espaço entre 
elas (ex.: 1/2;1/3).

Conceitos Básicos:
- A pena-base é ajustada pelas circunstâncias judiciais.
- A pena intermediária considera agravantes e atenuantes.
- A pena definitiva aplica aumentos e diminuições de pena.
- A pena de multa calcula todas as fases da dosimetria, de acordo com cada fração.

Concurso:
- O tipo de concurso influencia no cálculo (M = Material; F = Formal; C = Crime Continuado).

CONCURSO MATERIAL: a PPL e a multa serão somadas, podendo ser inseridas tantas quanto forem, 
ao clicar no símbolo + para abrir nova linha.

CONCURSO FORMAL: pode-se optar por colocar apenas a maior pena. Contudo, se todas forem 
inseridas, haverá o cálculo da pena de multa, na forma do art. 72 do Código Penal.
DICA: conforme o STJ, a fração de aumento deve corresponder à quantidade de crimes cumulados, 
da seguinte forma: aumento de 1/6 pela prática de 2 infrações; 1/5 para 3 infrações; 1/4 para 4 
infrações; 1/3 para 5 infrações; e 1/2 para 6 ou mais infrações (AgRg no HC 866667/SP).

CRIME CONTINUADO: pode ser inserida apenas a pena fixada, com a respectiva fração de aumento 
pela continuidade delitiva, e a pena de multa será exasperada (AgRg no AREsp 484.057/SP).
DICA: conforme o STJ, a fração de aumento no crime continuado deve corresponder à quantidade de 
crimes cumulados, da seguinte forma: aumento de 1/6 pela prática de 2 infrações; 1/5 para 3 
infrações; 1/4 para 4 infrações; 1/3 para 5 infrações; 1/2 para 6 infrações; e 2/3 para 7 ou mais 
infrações (HC 878122 / SP).

Desenvolvido por: Pedro Toaiari de Mattos Esterce (Juiz TJPR) - v.3.0
        """;

    JOptionPane.showMessageDialog(this, ajudaTexto, "Ajuda", JOptionPane.INFORMATION_MESSAGE);
}

    private void adicionarCampoPena(JPanel abaConcurso) {
        JTextField novaPena = new JTextField();
        listaPenas.add(novaPena);

        abaConcurso.add(new JLabel("Nova Pena:"));
        abaConcurso.add(novaPena);

        // Validação em tempo real da nova pena
        novaPena.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                validarFormatoPenaConcurso(novaPena);
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                validarFormatoPenaConcurso(novaPena);
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                validarFormatoPenaConcurso(novaPena);
            }
        });

        abaConcurso.revalidate();
        abaConcurso.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalculadoraDosimetria tela = new CalculadoraDosimetria();
            tela.setVisible(true);
        });
    }
}