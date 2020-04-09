package groovy.owl

@Grapes([
          @Grab(group='org.semanticweb.elk', module='elk-owlapi', version='0.4.2'),
          @Grab(group='net.sourceforge.owlapi', module='owlapi-api', version='4.1.0'),
          @Grab(group='net.sourceforge.owlapi', module='owlapi-apibinding', version='4.1.0'),
          @Grab(group='net.sourceforge.owlapi', module='owlapi-impl', version='4.1.0'),
          @Grab(group='net.sourceforge.owlapi', module='owlapi-parsers', version='4.1.0'),
          @GrabConfig(systemClassLoader=true)
        ])

import org.semanticweb.owlapi.model.parameters.*
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.elk.owlapi.ElkReasonerConfiguration
import org.semanticweb.elk.reasoner.config.*
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.reasoner.*
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.owllink.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.search.*;
import org.semanticweb.owlapi.manchestersyntax.renderer.*;
import org.semanticweb.owlapi.reasoner.structural.*


class OWLBuilder {
    def defaultNamespace
    def ns = [:]
    OWLOntology o 
    OWLOntologyManager manager
    OWLDataFactory fac
    OWLReasoner reasoner
    

    void initReasoner() {
	ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor()
	OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor)
	ElkReasonerFactory f1 = new ElkReasonerFactory()
	this.reasoner = f1.createReasoner(ont,config)
    }
    
    OWLBuilder() {
	this.manager = OWLManager.createOWLOntologyManager()
	this.fac = this.manager.getOWLDataFactory()
	this.o = this.manager.createOntology(IRI.create("http://aber-owl.net/test.owl"))
	initReasoner()
    }
    
    OWLBuilder(OWLOntology o) {
	this()
	this.o = o
	initReasoner()
    }
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
    def add = { OWLOntology o, OWLAxiom a -> this.manager.addAxiom(o, a) }
}
