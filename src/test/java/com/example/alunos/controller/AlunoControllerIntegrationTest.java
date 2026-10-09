package com.example.alunos.controller;

import com.example.alunos.dto.AlunoRequest;
import com.example.alunos.entity.Aluno;
import com.example.alunos.repository.AlunoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AlunoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AlunoRepository alunoRepository;

    @AfterEach
    void limparBanco() {
        alunoRepository.deleteAll();
    }

    @Test
    void deveCriarAlunoComStatus201() throws Exception {
        AlunoRequest alunoRequest = new AlunoRequest("Ana", "ana@email.com", 20);

        mockMvc.perform(post("/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alunoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Ana"));
    }

    @Test
    void deveBuscarAlunoComStatus200() throws Exception {
        Aluno aluno = salvarAluno("Bruno", "bruno@email.com", 21);

        mockMvc.perform(get("/alunos/{id}", aluno.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(aluno.getId()))
                .andExpect(jsonPath("$.email").value("bruno@email.com"));
    }

    @Test
    void deveAtualizarAlunoComStatus204() throws Exception {
        Aluno aluno = salvarAluno("Carla", "carla@email.com", 22);
        AlunoRequest alunoRequest = new AlunoRequest("Carla Lima", "lima@email.com", 23);

        mockMvc.perform(put("/alunos/{id}", aluno.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alunoRequest)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/alunos/{id}", aluno.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carla Lima"));
    }

    @Test
    void deveExcluirAlunoComStatus204() throws Exception {
        Aluno aluno = salvarAluno("Diego", "diego@email.com", 24);

        mockMvc.perform(delete("/alunos/{id}", aluno.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/alunos/{id}", aluno.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar404ParaAlunoInexistente() throws Exception {
        mockMvc.perform(get("/alunos/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Aluno não encontrado: 999"));
    }

    @Test
    void deveRetornar404AoAtualizarAlunoInexistente() throws Exception {
        AlunoRequest alunoRequest = new AlunoRequest("Eva", "eva@email.com", 25);

        mockMvc.perform(put("/alunos/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alunoRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar404AoExcluirAlunoInexistente() throws Exception {
        mockMvc.perform(delete("/alunos/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    private Aluno salvarAluno(String nome, String email, Integer idade) {
        Aluno aluno = new Aluno();
        aluno.setNome(nome);
        aluno.setEmail(email);
        aluno.setIdade(idade);
        return alunoRepository.save(aluno);
    }
}
