package io.github.sgpublic.xxpref

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated

class XXPrefKtProcessor(
    private val environment: SymbolProcessorEnvironment
): SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {

    }
}

@AutoService(SymbolProcessorProvider::class)
class XXPrefKtProcessorProvider: SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return XXPrefKtProcessor(environment)
    }
}