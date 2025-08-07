from fractions import Fraction
import tkinter as tk
from tkinter import messagebox, ttk

# Funções auxiliares

def validar_pena(event):
    """Valida o formato da pena em tempo real."""
    entrada = event.widget
    valor = entrada.get()
    if valor == "":
        mensagem.set("")
        return

    try:
        parse_pena(valor)
        mensagem.set("Formato válido.")
        mensagem_label.config(fg="green")
    except ValueError:
        mensagem.set("Formato inválido! Use algo como 2a3m15d20dm.")
        mensagem_label.config(fg="red")

def abrir_ajuda():
    """Exibe a janela de ajuda."""
    ajuda_janela = tk.Toplevel(janela)
    ajuda_janela.title("Ajuda")
    ajuda_texto = (
        "\nFormato de Entrada da Pena Mínima: use o formato 2a3m15d20dm para indicar 2 anos, 3 meses, 15 dias \n"
        "e 20 dias-multa. Não é necessário preencher todos os campos, sendo aceito, por exemplo: 2a10dm para pena \n"
        "mínima de 2 anos, com 10 dias-multa\n"
        
        "\nCampos Adicionais:\n"
        "\n\n"
        "- Circunstâncias negativas/positivas: são as circunstâncias judiciais, devendo ser inserida a \n"
        "quantidade. Por exemplo, se a culpabilidade e os motivos forem desfavoráveis, insira: 2 em \n"
        "negativas. O mesmo ocorre para as circunstâncias positivas (comportamento da vítima). \n"
        "\n\n"
        "- Agravantes/Atenuantes: o modo de aplicação é o mesmo das circunstâncias judiciais, devendo \n"
        "ser apontada a quantidade. No caso de valore iguais, a calculadora promoverá a compensação. \n"
        "Se, por exemplo, houver multirreincidência, deve ser inserida a quantidade de vezes que você \n"
        "pretende considerá-la, para que a compensação ocorra apenas de forma parcial. \n"
        "\n\n"
        "- Frações de aumento/diminuição: Use frações separadas por ponto e vírgula, sem epaço entre \n"
        "elas (ex.: 1/2;1/3).\n"
        "\n\n"       
        "\nConceitos Básicos:\n"
        "- A pena-base é ajustada pelas circunstâncias judiciais.\n"
        "- A pena intermediária considera agravantes e atenuantes.\n"
        "- A pena definitiva aplica aumentos e diminuições de pena.\n"
        "- A pena de multa calcula todas as fases da dosimetria, de acordo com cada fração \n"
        "\n\n"
        "\nConcurso:\n"
        "- O tipo de concurso influencia no cálculo (M = Material; F = Formal; C= Crime Continuado). \n"
        "\n\n"
        "CONCURSO MATERIAL: a PPL e a multa serão somadas, podendo ser inseridas tantas quanto forem, \n"
        "ao clicar no símbolo +, para abir nova linha. \n"
        "\n\n"
        "CONCURSO FORMAL: pode se optar por colocar apenas a maior pena. Contudo, se todas forem \n"
        "inseridas, haverá o cálculo da pena de multa, na forma do art. 72 do Código Penal. \n"
        "DICA: conforme o STJ, a fração de aumento deve corresponder à quantidade de crimes cumulados, \n"
        "da seguinte forma: aumento de 1/6 pela prática de 2 infrações; 1/5, para 3 infrações; 1/4 para 4 \n"
        "infrações; 1/3 para 5 infrações; e 1/2 para 6 ou mais infrações (AgRg no HC 866667/SP).  \n"
        "\n\n"
        "CRIME CONTINUADO: pode ser inserida apenas a pena fixada, com a respectiva fração de aumento \n"
        "pela continuidade delitiva, e a pena de multa será exasperada (AgRg no AREsp 484.057/SP).\n"
        "DICA: conforme o STJ, a fração de aumento no no crime continuado deve corresponder à quantidade de \n"
        "crimes cumulados, da seguinte forma:aumento de 1/6 pela prática de 2 infrações; 1/5, para 3 infrações; \n"
        "1/4 para 4 infrações; 1/3 para 5 infrações e 1/2 para 6 infrações; e 2/3, para 7 ou mais infrações \n"
        "(HC 878122 / SP). \n"
        "\n\n"
        "v.2. Possibilidade de escolher a fração de aumento. v.2.3. Corrreção de bugs no cálculo dos dias-multa. \n"
        
    )
    tk.Label(ajuda_janela, text=ajuda_texto, justify="left", padx=10, pady=10).pack()
    tk.Button(ajuda_janela, text="Fechar", command=ajuda_janela.destroy).pack(pady=10)

def limpar_campos():
    """Limpa todos os campos da interface."""
    entrada_pena.delete(0, tk.END)
    entrada_circ_neg.delete(0, tk.END)
    entrada_circ_pos.delete(0, tk.END)
    entrada_agravantes.delete(0, tk.END)
    entrada_atenuantes.delete(0, tk.END)
    entrada_aumentos.delete(0, tk.END)
    entrada_diminuicoes.delete(0, tk.END)
    resultado_texto.set("")
    fra_base_var.set("1/6")
    fra_intermediaria_var.set("1/6")

def adicionar_pena(aba_concurso, penas_entradas):
    """Adiciona um campo de entrada para mais uma pena e ajusta os widgets abaixo."""
    linha = len(penas_entradas) + 1
    nova_entrada = tk.Entry(aba_concurso, width=30)
    nova_entrada.grid(row=linha, column=1, sticky="w", pady=2)
    penas_entradas.append(nova_entrada)
    tk.Label(aba_concurso, text=f"Pena {len(penas_entradas)}:").grid(row=linha, column=0, sticky="w", pady=2)

    # Reorganizar os widgets abaixo
    label_fraçao.grid(row=linha + 1, column=0, sticky="w", pady=2)
    entrada_fraçao.grid(row=linha + 1, column=1, sticky="w", pady=2)
    botao_calcular.grid(row=linha + 2, column=0, columnspan=2, pady=10)
    label_soma.grid(row=linha + 3, column=0, columnspan=2, sticky="w", pady=2)
    label_exasperacao.grid(row=linha + 4, column=0, columnspan=2, sticky="w", pady=2)

def calcular():
    """Realiza o cálculo principal de dosimetria, incluindo dias-multa."""
    try:
        # Entrada dos dados
        pena_min_input = entrada_pena.get() or "0a0m0d0dm"
        circunstancias_neg = int(entrada_circ_neg.get() or 0)
        circunstancias_pos = int(entrada_circ_pos.get() or 0)
        agravantes = int(entrada_agravantes.get() or 0)
        atenuantes = int(entrada_atenuantes.get() or 0)
        aumentos = entrada_aumentos.get() or ""
        diminuicoes = entrada_diminuicoes.get() or ""

        # Parse da pena mínima
        pena_min = parse_pena(pena_min_input)
        fracao_base = float(Fraction(fra_base_var.get()))
        fracao_intermediaria = float(Fraction(fra_intermediaria_var.get()))

        # Dias-multa da pena mínima
        dias_multa_min = pena_min.get('dm', 0)

        # Calcular pena-base (PPL e dias-multa)
        pena_min_dias = converter_para_dias(pena_min)
        pena_base_dias = pena_min_dias + (pena_min_dias * circunstancias_neg * fracao_base)
        pena_base_dias -= (pena_min_dias * circunstancias_pos * fracao_base)
        pena_base_dias = round(pena_base_dias)

        dias_multa_base = dias_multa_min + (dias_multa_min * circunstancias_neg * fracao_base)
        dias_multa_base -= (dias_multa_min * circunstancias_pos * fracao_base)
        dias_multa_base = max(0, round(dias_multa_base))

        # Calcular pena intermediária (PPL e dias-multa)
        if agravantes > atenuantes:
            restante = agravantes - atenuantes
            pena_intermediaria_dias = pena_base_dias + (pena_min_dias * restante * fracao_intermediaria)
            dias_multa_intermediarios = dias_multa_base + (dias_multa_min * restante * fracao_intermediaria)
        elif atenuantes > agravantes:
            restante = atenuantes - agravantes
            pena_intermediaria_dias = pena_base_dias - (pena_min_dias * restante * fracao_intermediaria)
            dias_multa_intermediarios = dias_multa_base - (dias_multa_min * restante * fracao_intermediaria)

            # Não pode reduzir abaixo do mínimo legal
            pena_intermediaria_dias = max(pena_intermediaria_dias, pena_min_dias)
            dias_multa_intermediarios = max(dias_multa_intermediarios, dias_multa_min)
        else:
            pena_intermediaria_dias = pena_base_dias
            dias_multa_intermediarios = dias_multa_base

        pena_intermediaria_dias = round(pena_intermediaria_dias)
        dias_multa_intermediarios = max(0, round(dias_multa_intermediarios))

        # Calcular pena definitiva (PPL e dias-multa)
        pena_definitiva_dias = pena_intermediaria_dias
        dias_multa_definitivos = dias_multa_intermediarios

        for aumento in aumentos.split(';'):
            if aumento:
                pena_definitiva_dias += round(pena_intermediaria_dias * float(Fraction(aumento)))
                dias_multa_definitivos += round(dias_multa_intermediarios * float(Fraction(aumento)))

        for diminuicao in diminuicoes.split(';'):
            if diminuicao:
                pena_definitiva_dias -= round(pena_definitiva_dias * float(Fraction(diminuicao)))
                dias_multa_definitivos -= round(dias_multa_definitivos * float(Fraction(diminuicao)))

        pena_definitiva_dias = round(pena_definitiva_dias)
        dias_multa_definitivos = max(0, round(dias_multa_definitivos))

        # Exibir resultados
        resultado_texto.set(
            f"Pena-base: {converter_para_formatado(pena_base_dias)}, Dias-multa: {dias_multa_base}\n"
            f"Pena intermediária: {converter_para_formatado(pena_intermediaria_dias)}, Dias-multa: {dias_multa_intermediarios}\n"
            f"Pena definitiva: {converter_para_formatado(pena_definitiva_dias)}, Dias-multa: {dias_multa_definitivos}"
        )

    except ValueError as e:
        messagebox.showerror("Erro de entrada", str(e))
    except Exception as e:
        messagebox.showerror("Erro inesperado", str(e))

def calcular_concurso():
    """Realiza o cálculo de concursos."""
    try:
        concurso_tipo = concurso_var.get()  # Tipo de concurso: M, F ou C
        fração_exasperacao = entrada_fraçao.get() or ""

        penas = []
        for entrada in penas_entradas:
            pena_str = entrada.get()
            if pena_str:
                penas.append(parse_pena(pena_str))

        if not penas:
            raise ValueError("Insira ao menos uma pena.")

        # Somar as penas e dias-multa
        total_dias = sum(converter_para_dias(pena) for pena in penas)
        total_dm = sum(pena.get('dm', 0) for pena in penas)

        if concurso_tipo == "M":  # Concurso Material
            resultado_soma.set(f"Soma: {converter_para_formatado(total_dias)}, Dias-multa: {total_dm}dm")
            resultado_exasperacao.set("")
        else:  # Concurso Formal ou Crime Continuado
            maior_pena = max(penas, key=converter_para_dias)
            dias_maior_pena = converter_para_dias(maior_pena)
            dias_multa_maior_pena = maior_pena.get('dm', 0)

            if fração_exasperacao:
                aumento_dias = round(dias_maior_pena * float(Fraction(fração_exasperacao)))
                aumento_dm = round(dias_multa_maior_pena * float(Fraction(fração_exasperacao)))

                pena_exasperada_dias = dias_maior_pena + aumento_dias
                dias_multa_exasperados = dias_multa_maior_pena + aumento_dm

                resultado_exasperacao.set(
                    f"Pena exasperada: {converter_para_formatado(pena_exasperada_dias)}, Dias-multa: {dias_multa_exasperados}dm"
                )
                resultado_soma.set("")
            else:
                raise ValueError("Insira a fração de exasperação para concurso formal ou crime continuado.")
    except ValueError as e:
        messagebox.showerror("Erro de entrada", str(e))
    except Exception as e:
        messagebox.showerror("Erro inesperado", str(e))

def parse_pena(pena_str):
    import re
    match = re.match(r"^(?:(\d+)a)?(?:(\d+)m)?(?:(\d+)d)?(?:(\d+)dm)?$", pena_str)
    if not match:
        raise ValueError("Formato inválido de pena. Use algo como 2a3m15d20dm.")
    anos, meses, dias, dias_multa = match.groups()
    return {
        'a': int(anos) if anos else 0,
        'm': int(meses) if meses else 0,
        'd': int(dias) if dias else 0,
        'dm': int(dias_multa) if dias_multa else 0
    }

def converter_para_dias(pena):
    anos = pena.get('a', 0)
    meses = pena.get('m', 0)
    dias = pena.get('d', 0)
    return anos * 360 + meses * 30 + dias  # Adotando 360 dias no ano

def converter_para_formatado(dias):
    anos = dias // 360  # Adotando 360 dias no ano
    dias %= 360
    meses = dias // 30
    dias %= 30
    return f"{anos}a{meses}m{dias}d"

# Interface gráfica principal
janela = tk.Tk()
janela.title("Calculadora de Dosimetria Penal")

abas = ttk.Notebook(janela)

# Aba principal (dosimetria básica)
aba_principal = ttk.Frame(abas)
abas.add(aba_principal, text="Dosimetria")

mensagem = tk.StringVar()
mensagem.set("")

# Campos de entrada
label_pena = tk.Label(aba_principal, text="Pena mínima (ex: 2a3m15d20dm):")
label_pena.grid(row=0, column=0, sticky="w")

entrada_pena = tk.Entry(aba_principal, width=30)
entrada_pena.grid(row=0, column=1)
entrada_pena.bind("<KeyRelease>", validar_pena)

mensagem_label = tk.Label(aba_principal, textvariable=mensagem)
mensagem_label.grid(row=0, column=2, sticky="w")

# Botão de ajuda e limpar
botao_ajuda = tk.Button(aba_principal, text="Ajuda", command=abrir_ajuda)
botao_ajuda.grid(row=11, column=2, pady=10, padx=5)

botao_limpar = tk.Button(aba_principal, text="Limpar", command=limpar_campos)
botao_limpar.grid(row=10, column=2, pady=10, padx=5)

# Outros campos
label_circ_neg = tk.Label(aba_principal, text="Circunstâncias negativas:")
label_circ_neg.grid(row=2, column=0, sticky="w")
entrada_circ_neg = tk.Entry(aba_principal, width=5)
entrada_circ_neg.grid(row=2, column=1, sticky="w")

label_circ_pos = tk.Label(aba_principal, text="Circunstâncias positivas:")
label_circ_pos.grid(row=3, column=0, sticky="w")
entrada_circ_pos = tk.Entry(aba_principal, width=5)
entrada_circ_pos.grid(row=3, column=1, sticky="w")

fra_base_var = tk.StringVar(value="1/6")
label_fra_base = tk.Label(aba_principal, text="Fração de Aumento (Pena-base):")
label_fra_base.grid(row=4, column=0, sticky="w")
fra_base_opcoes = ttk.Combobox(aba_principal, textvariable=fra_base_var, values=["1/6", "1/8"], state="readonly")
fra_base_opcoes.grid(row=4, column=1, sticky="w")

label_agravantes = tk.Label(aba_principal, text="Agravantes:")
label_agravantes.grid(row=5, column=0, sticky="w")
entrada_agravantes = tk.Entry(aba_principal, width=5)
entrada_agravantes.grid(row=5, column=1, sticky="w")

label_atenuantes = tk.Label(aba_principal, text="Atenuantes:")
label_atenuantes.grid(row=6, column=0, sticky="w")
entrada_atenuantes = tk.Entry(aba_principal, width=5)
entrada_atenuantes.grid(row=6, column=1, sticky="w")

fra_intermediaria_var = tk.StringVar(value="1/6")
label_fra_intermediaria = tk.Label(aba_principal, text="Fração de Aumento (Pena Intermediária):")
label_fra_intermediaria.grid(row=7, column=0, sticky="w")
fra_intermediaria_opcoes = ttk.Combobox(aba_principal, textvariable=fra_intermediaria_var, values=["1/6", "1/8"], state="readonly")
fra_intermediaria_opcoes.grid(row=7, column=1, sticky="w")

label_aumentos = tk.Label(aba_principal, text="Frações de aumento (ex: 1/2;1/3):")
label_aumentos.grid(row=8, column=0, sticky="w")
entrada_aumentos = tk.Entry(aba_principal, width=30)
entrada_aumentos.grid(row=8, column=1)

label_diminuicoes = tk.Label(aba_principal, text="Frações de diminuição (ex: 1/5;1/4):")
label_diminuicoes.grid(row=9, column=0, sticky="w")
entrada_diminuicoes = tk.Entry(aba_principal, width=30)
entrada_diminuicoes.grid(row=9, column=1)

botao_calcular_principal = tk.Button(aba_principal, text="Calcular", command=calcular)
botao_calcular_principal.grid(row=10, column=1, columnspan=1, pady=10)

resultado_texto = tk.StringVar()
resultado_texto.set("")
resultado_label = tk.Label(aba_principal, textvariable=resultado_texto, justify="left")
resultado_label.grid(row=11, column=0, columnspan=2)

# Aba adicional para concursos
aba_concurso = ttk.Frame(abas)
abas.add(aba_concurso, text="Concurso")

# Configuração da aba de concurso
tk.Label(aba_concurso, text="Concurso (M, F, C):").grid(row=0, column=0, sticky="w")
concurso_var = tk.StringVar(value="M")
entrada_concurso = tk.Entry(aba_concurso, textvariable=concurso_var, width=5)
entrada_concurso.grid(row=0, column=1, sticky="w")

tk.Label(aba_concurso, text="Pena 1:").grid(row=1, column=0, sticky="w")
penas_entradas = []
entrada_pena1 = tk.Entry(aba_concurso, width=30)
entrada_pena1.grid(row=1, column=1, sticky="w")
penas_entradas.append(entrada_pena1)

tk.Button(aba_concurso, text="+", command=lambda: adicionar_pena(aba_concurso, penas_entradas)).grid(row=1, column=2, sticky="w")

label_fraçao = tk.Label(aba_concurso, text="Fração de Exasperação (ex: 1/6):")
label_fraçao.grid(row=2, column=0, sticky="w")
entrada_fraçao = tk.Entry(aba_concurso, width=10)
entrada_fraçao.grid(row=2, column=1, sticky="w")

botao_calcular = tk.Button(aba_concurso, text="Calcular", command=calcular_concurso)
botao_calcular.grid(row=3, column=0, columnspan=2, pady=10)

resultado_soma = tk.StringVar()
resultado_soma.set("")
label_soma = tk.Label(aba_concurso, textvariable=resultado_soma, justify="left")
label_soma.grid(row=4, column=0, columnspan=2, sticky="w")

resultado_exasperacao = tk.StringVar()
resultado_exasperacao.set("")
label_exasperacao = tk.Label(aba_concurso, textvariable=resultado_exasperacao, justify="left")
label_exasperacao.grid(row=5, column=0, columnspan=2, sticky="w")

abas.pack(expand=1, fill="both")

# Adicionar marca d'água
marca_dagua = tk.Label(janela, text="Desenvolvido por: Pedro Toaiari de Mattos Esterce (Juiz TJPR) - v.2.5", font=("Arial", 8), fg="gray")
marca_dagua.pack(side="bottom", anchor="se", pady=5, padx=5)

janela.mainloop()

