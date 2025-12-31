package com.amorabot.inscripted.item.inscription;

public interface InscriptionVisitor<T> {
    T visitProceduralInscription(ProceduralInscription proceduralInscription);
    T visitUniqueInscription(UniqueInscription uniqueInscription);
}
