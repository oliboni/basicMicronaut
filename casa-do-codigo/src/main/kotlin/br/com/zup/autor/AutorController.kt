package br.com.zup.autor

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Controller("/autores")
class AutorController(val autorRepository: AutorRepository) {

    @Post
    @Transactional
    fun cadastra(@Body @Valid request: NovoAutorRequest): HttpStatus {
        val autor = request.toAutor()
        autorRepository.save(autor)
        println("Autor cadastrado com sucesso!")
        return HttpStatus.CREATED
    }

    @Get()
    fun buscaAutor(@QueryValue(defaultValue = "") email: String): HttpResponse<Any> {

        if (email.isEmpty()) {
            val autores = autorRepository.findAll()
            val listAutores = autores.map { autor -> DetalhesDoAutorResponse(autor) }
            return HttpResponse.ok(listAutores)
        }

//        val autor = autorRepository.findByEmail(email)
        val autor = autorRepository.buscaPorEmail(email)
        if (autor.isEmpty) {
            return HttpResponse.notFound()
        }

        return HttpResponse.ok(DetalhesDoAutorResponse(autor.get()))
    }

    @Put("/{id}")
    @Transactional
    fun atualizaAutor(@PathVariable id: Long, descricao: String): HttpResponse<Any>{
        val autor = autorRepository.findById(id)
        if (autor.isEmpty){
            return HttpResponse.notFound()
        }

        autor.get().descricao = descricao
/**
 *  Devido estar usando o @Transactional, deixamos está transação aberta então o update é feito de forma implicita, não necessitando chamar o update
 */
//        autorRepository.update(autor.get())

        return HttpResponse.ok(DetalhesDoAutorResponse(autor.get()))
    }

    @Delete("/{id}")
    @Transactional
    fun deletaAutor(@PathVariable id: Long): HttpResponse<Any>{
        val autor = autorRepository.findById(id)
        if (autor.isEmpty){
            return HttpResponse.notFound()
        }

        autorRepository.delete(autor.get())
        return HttpResponse.ok()
    }
}