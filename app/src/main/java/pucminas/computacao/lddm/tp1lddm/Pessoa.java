package pucminas.computacao.lddm.tp1lddm;

import java.lang.ref.SoftReference;

public class Pessoa {
    private String nomePessoa;
    private String numeroTel;
    private String endereco;
    private String emailPessoa;

    Pessoa(String nomePessoa, String numeroTel, String endereco, String emailPessoa){
        setNomePessoa(nomePessoa);
        setNumeroTel(numeroTel);
        setEndereco(endereco);
        setEmailPessoa(emailPessoa);
    }

    public void setNomePessoa(String nomePessoa){
        this.nomePessoa = nomePessoa;
    }

    public void setNumeroTel(String numeroTel){
        this.numeroTel = numeroTel;
    }

    public void setEndereco(String endereco){
        this.endereco = endereco;
    }

    public void setEmailPessoa(String emailPessoa){
        this.emailPessoa = emailPessoa;
    }

    public String getNomePessoa(){
        return this.nomePessoa;
    }

    public String getNumeroTel(){
        return this.numeroTel;
    }

    public String getEndereco(){
        return this.endereco;
    }

    public String getEmailPessoa(){
        return this.emailPessoa;
    }
}
