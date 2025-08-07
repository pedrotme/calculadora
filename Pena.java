public class Pena {
    
    private int anos;
    private int meses;
    private int dias;
    private int diasMulta;

    public Pena(int anos, int meses, int dias, int diasMulta) {
        this.anos = anos;
        this.meses = meses;
        this.dias = dias;
        this.diasMulta = diasMulta;
    }

    public int getAnos() { return anos; }
    public int getMeses() { return meses; }
    public int getDias() { return dias; }
    public int getDiasMulta() { return diasMulta; }

    public int converterParaDias() {
        return (anos * 360) + (meses * 30) + dias;
    }

    public static Pena parse(String texto) {
    if (texto == null || texto.trim().isEmpty()) {
        throw new IllegalArgumentException("Informe a pena no formato correto.");
    }

    int anos = 0;
    int meses = 0;
    int dias = 0;
    int diasMulta = 0;

    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
        "(\\d+)a|" +           // Captura anos
        "(\\d+)m(?![a-zA-Z])|" + // Captura meses, não seguido de outra letra
        "(\\d+)d(?!m)|" +      // Captura dias, não seguido de "m"
        "(\\d+)dm"             // Captura dias-multa
    );

    java.util.regex.Matcher matcher = pattern.matcher(texto);

    while (matcher.find()) {
        if (matcher.group(1) != null) {
            anos = Integer.parseInt(matcher.group(1));
        } else if (matcher.group(2) != null) {
            meses = Integer.parseInt(matcher.group(2));
        } else if (matcher.group(3) != null) {
            dias = Integer.parseInt(matcher.group(3));
        } else if (matcher.group(4) != null) {
            diasMulta = Integer.parseInt(matcher.group(4));
        }
    }

    if (anos == 0 && meses == 0 && dias == 0 && diasMulta == 0) {
        throw new IllegalArgumentException("Formato inválido de pena. Utilize algo como 2a3m15d20dm.");
    }

    return new Pena(anos, meses, dias, diasMulta);
}


    public static String formatarDias(int totalDias) {
        int anos = totalDias / 360;
        int resto = totalDias % 360;
        int meses = resto / 30;
        int dias = resto % 30;

        return String.format("%da%dm%dd", anos, meses, dias);
    }
}
