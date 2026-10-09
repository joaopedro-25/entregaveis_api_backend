package com.example.alunos.service;

import com.example.alunos.dto.AlunoRequest;
import com.example.alunos.dto.AlunoResponse;
import com.example.alunos.entity.Aluno;
import com.example.alunos.exception.ResourceNotFoundException;
import com.example.alunos.repository.AlunoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public AlunoResponse create(AlunoRequest alunoRequest) {
        Aluno aluno = new Aluno();
        preencherAluno(aluno, alunoRequest);
        Aluno alunoSalvo = alunoRepository.save(aluno);
        return converterParaResponse(alunoSalvo);
    }

    public AlunoResponse getById(Long id) {
        Optional<Aluno> alunoOptional = alunoRepository.findById(id);

        if (alunoOptional.isPresent()) {
            Aluno aluno = alunoOptional.get();
            return converterParaResponse(aluno);
        }

        throw alunoNaoEncontrado(id);
    }

    public void update(Long id, AlunoRequest alunoRequest) {
        Aluno aluno = alunoRepository.findById(id).orElseThrow(() -> alunoNaoEncontrado(id));
        preencherAluno(aluno, alunoRequest);
        alunoRepository.save(aluno);
    }

    public void deleteById(Long id) {
        boolean existe = alunoRepository.existsById(id);
        if (!existe) {
            throw alunoNaoEncontrado(id);
        }

        alunoRepository.deleteById(id);
    }

    private void preencherAluno(Aluno aluno, AlunoRequest alunoRequest) {
        aluno.setNome(alunoRequest.getNome());
        aluno.setEmail(alunoRequest.getEmail());
        aluno.setIdade(alunoRequest.getIdade());
    }

    private AlunoResponse converterParaResponse(Aluno aluno) {
        return new AlunoResponse(aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getIdade());
    }

    private ResourceNotFoundException alunoNaoEncontrado(Long id) {
        return new ResourceNotFoundException("Aluno não encontrado: " + id);
    }
}
