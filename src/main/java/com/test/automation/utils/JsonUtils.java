package com.test.automation.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Leitura de dados de teste a partir de arquivos JSON, usando a biblioteca Jackson.
 */
public class JsonUtils {

    // O ObjectMapper é o "tradutor" do Jackson: converte JSON <-> objetos Java
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtils() {
    }

    /**
     * Lê um arquivo JSON que contém uma lista e devolve os itens já convertidos.
     * Exemplo: JsonUtils.readList(caminho, Product.class) -> List<Product>
     *
     * @param filePath caminho do arquivo JSON
     * @param type     classe de cada item da lista (ex.: Product.class)
     */
    public static <T> List<T> readList(String filePath, Class<T> type) {
        Log.info("Lendo dados de teste do JSON: " + filePath);
        try {
            return MAPPER.readValue(new File(filePath),
                    MAPPER.getTypeFactory().constructCollectionType(List.class, type));
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo JSON: " + filePath, e);
        }
    }
}