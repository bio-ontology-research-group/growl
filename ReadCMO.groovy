@Grapes([
    @Grab(group='org.semanticweb.elk', module='elk-owlapi', version='0.4.3'),
    @Grab(group='net.sourceforge.owlapi', module='owlapi-api', version='5.1.13'),
    @Grab(group='net.sourceforge.owlapi', module='owlapi-apibinding', version='5.1.13'),
    @Grab(group='net.sourceforge.owlapi', module='owlapi-impl', version='5.1.13'),
    @Grab(group='net.sourceforge.owlapi', module='owlapi-parsers', version='5.1.13'),
    @Grab(group='net.sourceforge.owlapi', module='owlapi-distribution', version='5.1.13'),
    @GrabConfig(systemClassLoader=true)
])

import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.parameters.*
import org.semanticweb.elk.owlapi.ElkReasonerFactory
import org.semanticweb.elk.owlapi.ElkReasonerConfiguration
import org.semanticweb.elk.reasoner.config.*
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.reasoner.*
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary
import org.semanticweb.owlapi.model.*
import org.semanticweb.owlapi.io.*
import org.semanticweb.owlapi.owllink.*
import org.semanticweb.owlapi.util.*
import org.semanticweb.owlapi.search.*
import org.semanticweb.owlapi.manchestersyntax.renderer.*
import org.semanticweb.owlapi.reasoner.structural.*


def and = { cl1, cl2 ->
    fac.getOWLObjectIntersectionOf(cl1,cl2)
}
def some = { r, cl ->
    fac.getOWLObjectSomeValuesFrom(r,cl)
}
def equiv = { cl1, cl2 ->
    fac.getOWLEquivalentClassesAxiom(cl1, cl2)
}
def subclass = { cl1, cl2 ->
    fac.getOWLSubClassOfAxiom(cl1, cl2)
}
def R = { String s ->
    if (s == "part-of") {
	fac.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/BFO_0000050"))
    } else if (s == "has-part") {
	fac.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/BFO_0000051"))
    } else {
	fac.getOWLObjectProperty(IRI.create("http://aber-owl.net/#"+s))
    }
}
def C = { String s ->
    fac.getOWLClass(IRI.create(onturi+s))
}
//def add = { OWLOntology o, OWLAxiom a -> this.manager.addAxiom(o, a) }

def labelMap = { ont ->
    def map = [:]
    def fac = OWLManager.createOWLOntologyManager().getOWLDataFactory()
    ont.getClassesInSignature(true).each { cl ->
	EntitySearcher.getAnnotations(cl, ont, fac.getRDFSLabel()).each { anno ->
	    OWLAnnotationValue val = anno.getValue()
	    if (val instanceof OWLLiteral) {
		map[cl] = val.getLiteral()
	    }
	}
    }
    map
}

def manager = OWLManager.createOWLOntologyManager()
def fac = manager.getOWLDataFactory()

def cmo = manager.loadOntologyFromOntologyDocument(new File("cmo.owl"))
def uberon = manager.loadOntologyFromOntologyDocument(new File("uberon.owl"))
def chebi = manager.loadOntologyFromOntologyDocument(new File("chebi.owl"))
def pato = manager.loadOntologyFromOntologyDocument(new File("pato.owl"))
ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor()
OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor)
ElkReasonerFactory f1 = new ElkReasonerFactory()
def reasoner = f1.createReasoner(cmo,config)

def cmolabels = labelMap(cmo)
def uberonlabels = labelMap(uberon)
def chebilabels = labelMap(chebi)
def patolabels = labelMap(pato)

def cmo2other = [:].withDefault { new LinkedHashSet() }

cmolabels.each { cl, lab ->
    uberonlabels.each { k, v ->
	if (lab.indexOf(v)>-1) {
	    println "$lab\t$k\t$cl"
	}
    }
    chebilabels.each { k, v ->
	if (lab.indexOf(v)>-1) {
	    println "$lab\t$k\t$cl"
	}
    }
    patolabels.each { k, v ->
	if (lab.indexOf(v)>-1) {
	    println "$lab\t$k\t$cl"
	}
    }
}
