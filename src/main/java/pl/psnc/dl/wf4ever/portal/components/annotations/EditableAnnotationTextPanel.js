var dcterms = {
	'dcterms:abstract' : 'http://purl.org/dc/terms/abstract',
	'dcterms:accessRights' : 'http://purl.org/dc/terms/accessRights',
	'dcterms:accrualMethod' : 'http://purl.org/dc/terms/accrualMethod',
	'dcterms:accrualPeriodicity' : 'http://purl.org/dc/terms/accrualPeriodicity',
	'dcterms:accrualPolicy' : 'http://purl.org/dc/terms/accrualPolicy',
	'dcterms:alternative' : 'http://purl.org/dc/terms/alternative',
	'dcterms:audience' : 'http://purl.org/dc/terms/audience',
	'dcterms:available' : 'http://purl.org/dc/terms/available',
	'dcterms:bibliographicCitation' : 'http://purl.org/dc/terms/bibliographicCitation',
	'dcterms:conformsTo' : 'http://purl.org/dc/terms/conformsTo',
	'dcterms:contributor' : 'http://purl.org/dc/terms/contributor',
	'dcterms:coverage' : 'http://purl.org/dc/terms/coverage',
	'dcterms:created' : 'http://purl.org/dc/terms/created',
	'dcterms:creator' : 'http://purl.org/dc/terms/creator',
	'dcterms:date' : 'http://purl.org/dc/terms/date',
	'dcterms:dateAccepted' : 'http://purl.org/dc/terms/dateAccepted',
	'dcterms:dateCopyrighted' : 'http://purl.org/dc/terms/dateCopyrighted',
	'dcterms:dateSubmitted' : 'http://purl.org/dc/terms/dateSubmitted',
	'dcterms:description' : 'http://purl.org/dc/terms/description',
	'dcterms:educationLevel' : 'http://purl.org/dc/terms/educationLevel',
	'dcterms:extent' : 'http://purl.org/dc/terms/extent',
	'dcterms:format' : 'http://purl.org/dc/terms/format',
	'dcterms:hasFormat' : 'http://purl.org/dc/terms/hasFormat',
	'dcterms:hasPart' : 'http://purl.org/dc/terms/hasPart',
	'dcterms:hasVersion' : 'http://purl.org/dc/terms/hasVersion',
	'dcterms:identifier' : 'http://purl.org/dc/terms/identifier',
	'dcterms:instructionalMethod' : 'http://purl.org/dc/terms/instructionalMethod',
	'dcterms:isFormatOf' : 'http://purl.org/dc/terms/isFormatOf',
	'dcterms:isPartOf' : 'http://purl.org/dc/terms/isPartOf',
	'dcterms:isReferencedBy' : 'http://purl.org/dc/terms/isReferencedBy',
	'dcterms:isReplacedBy' : 'http://purl.org/dc/terms/isReplacedBy',
	'dcterms:isRequiredBy' : 'http://purl.org/dc/terms/isRequiredBy',
	'dcterms:issued' : 'http://purl.org/dc/terms/issued',
	'dcterms:isVersionOf' : 'http://purl.org/dc/terms/isVersionOf',
	'dcterms:language' : 'http://purl.org/dc/terms/language',
	'dcterms:license' : 'http://purl.org/dc/terms/license',
	'dcterms:mediator' : 'http://purl.org/dc/terms/mediator',
	'dcterms:medium' : 'http://purl.org/dc/terms/medium',
	'dcterms:modified' : 'http://purl.org/dc/terms/modified',
	'dcterms:provenance' : 'http://purl.org/dc/terms/provenance',
	'dcterms:publisher' : 'http://purl.org/dc/terms/publisher',
	'dcterms:references' : 'http://purl.org/dc/terms/references',
	'dcterms:relation' : 'http://purl.org/dc/terms/relation',
	'dcterms:replaces' : 'http://purl.org/dc/terms/replaces',
	'dcterms:requires' : 'http://purl.org/dc/terms/requires',
	'dcterms:rights' : 'http://purl.org/dc/terms/rights',
	'dcterms:rightsHolder' : 'http://purl.org/dc/terms/rightsHolder',
	'dcterms:source' : 'http://purl.org/dc/terms/source',
	'dcterms:spatial' : 'http://purl.org/dc/terms/spatial',
	'dcterms:subject' : 'http://purl.org/dc/terms/subject',
	'dcterms:tableOfContents' : 'http://purl.org/dc/terms/tableOfContents',
	'dcterms:temporal' : 'http://purl.org/dc/terms/temporal',
	'dcterms:title' : 'http://purl.org/dc/terms/title',
	'dcterms:type' : 'http://purl.org/dc/terms/type',
	'dcterms:valid' : 'http://purl.org/dc/terms/valid'
};

var rdfs = {
	'rdfs:comment' : 'http://www.w3.org/2000/01/rdf-schema#comment'
};

var rdf = {
		'rdf:type' : 'http://www.w3.org/1999/02/22-rdf-syntax-ns#type'
	};

var roterms = {
	'roterms:performsTask' : 'http://purl.org/wf4ever/roterms#performsTask',
	'roterms:inputSelected' : 'http://purl.org/wf4ever/roterms#inputSelected',
	'roterms:technicalContact' : 'http://purl.org/wf4ever/roterms#technicalContact',
	'roterms:requiresHardware': 'http://purl.org/wf4ever/roterms#requiresHardware',
	'roterms:requiresSoftware': 'http://purl.org/wf4ever/roterms#requiresSoftware', 
	'roterms:requiresDataset': 'http://purl.org/wf4ever/roterms#requiresDataset',
	'roterms:sampleSize': 'http://purl.org/wf4ever/roterms#sampleSize',
	'roterms:previousWorkflow': 'http://purl.org/wf4ever/roterms#previousWorkflow',
	'roterms:subsequentWorkflow': 'http://purl.org/wf4ever/roterms#subsequentWorkflow'
};

var wfprov = {
	'wfprov:durationInSeconds' : 'http://purl.org/wf4ever/wfdesc#durationInSeconds',
};

var properties = $.extend({}, dcterms, rdfs, rdf, roterms, wfprov);

var source = function() {
	return Object.keys(properties);
};

var updater = function(item) {
	return properties[item];
};

var options = {
	source : source,
	updater : updater
};
