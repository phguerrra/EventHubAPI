package com.grupo1.backGrupo1.util;

public class CpfValidator {

    public static boolean isValid(String cpf) {

        // remove caracteres (., -)
        cpf = cpf.replaceAll("[^0-9]", "");

        // verifica tamanho
        if (cpf.length() != 11) return false;

        // evita sequências iguais (11111111111)
        if (cpf.matches("(\\d)\\1{10}")) return false;

        int soma = 0;

        // cálculo do primeiro dígito
        for (int i = 0; i < 9; i++) {
            soma += (cpf.charAt(i) - '0') * (10 - i);
        }

        int digito1 = 11 - (soma % 11);
        if (digito1 >= 10) digito1 = 0;

        // cálculo do segundo dígito
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += (cpf.charAt(i) - '0') * (11 - i);
        }

        int digito2 = 11 - (soma % 11);
        if (digito2 >= 10) digito2 = 0;

        // valida final
        return digito1 == (cpf.charAt(9) - '0') &&
                digito2 == (cpf.charAt(10) - '0');
    }
}