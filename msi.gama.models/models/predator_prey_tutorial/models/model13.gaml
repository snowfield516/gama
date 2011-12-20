model prey_predator
// gen by Xml2Gaml

global {
	var nb_preys_init type: int init: 200 min: 1 max: 1000 parameter: 'Initial number of preys: ' category: 'Prey' ;
	var nb_predator_init type: int init: 20 min: 0 max: 200 parameter: 'Initial number of predators ' category: 'Predator' ;
	var map_init type: string init: '../includes/gis/vegetation.shp' parameter: 'Initial environement: ' category: 'Environment' ;
	var prey_max_energy type: float init: 1 parameter: 'Prey max energy: ' category: 'Prey' ;
	var prey_max_transfert type: float init: 0.1 parameter: 'Prey max transfert: ' category: 'Prey' ;
	var prey_energy_consum type: float init: 0.05 parameter: 'Prey energy consumption: ' category: 'Prey' ;
	var prey_energy_reproduce type: float init: 0.5 parameter: 'Prey energy reproduce: ' category: 'Prey' ;
	var prey_proba_reproduce type: float init: 0.01 parameter: 'Prey probability reproduce: ' category: 'Prey' ;
	var prey_nb_max_offsprings type: int init: 5 parameter: 'Prey nb max offsprings: ' category: 'Prey' ;
	var prey_speed type: float init: 10.0 parameter: 'Prey speed: ' category: 'Prey' ;
	var predator_max_energy type: float init: 1 parameter: 'Predator max energy: ' category: 'Predator' ;
	var predator_energy_transfert type: float init: 0.5 parameter: 'Predator energy transfert: ' category: 'Predator' ;
	var predator_energy_consum type: float init: 0.02 parameter: 'Predator energy consumption: ' category: 'Predator' ;
	var predator_energy_reproduce type: float init: 0.5 parameter: 'Predator energy reproduce: ' category: 'Predator' ;
	var predator_proba_reproduce type: float init: 0.01 parameter: 'Predator probability reproduce: ' category: 'Predator' ;
	var predator_nb_max_offsprings type: int init: 3 parameter: 'Predator nb max offsprings: ' category: 'Predator' ;
	var predator_range type: float init: 10.0 parameter: 'Predator range: ' category: 'Predator' ;
	var predator_speed type: float init: 10.0 parameter: 'Predator speed: ' category: 'Predator' ;
	var nb_preys type: int value: length (prey as list) init: nb_preys_init ;
	var nb_predators type: int value: length (predator as list) init: nb_predator_init ;

	init {
		create species: vegetation from: map_init with: [food::read ('FOOD'), foodProd::read ('FOOD_PROD')] ;
		create species: prey number: nb_preys {
			set myPatch value: one_of(vegetation as list);
			set location value: any_location_in(myPatch.shape);
		}
		create species: predator number: nb_predators {
			set myPatch value: one_of(vegetation as list);
			set location value: any_location_in(myPatch.shape);
		}
	}
	reflex stop_simulation when: (nb_preys = 0) or (nb_predators = 0) {
		do action: halt ;
	}
}
entities {
	species generic_species  skills: [moving] {
		const size type: float init: 2 ;
		const color type: rgb;
		const max_energy type: float;
		const energy_consum type: float;
		const energy_reproduce type: float ;
		const proba_reproduce type: float ;
		const nb_max_offsprings type: int ;
		const my_icon type: string ;
		var myPatch type: vegetation init: nil ;
		var energy type: float init: (rnd(1000) / 1000) * max_energy  value: energy - energy_consum max: max_energy ;
		var speed type: float ;
		reflex basic_move {
			do action: wander {
				arg bounds value: myPatch ;
				arg speed value: speed;
			}
		}
		action choose_cell ;
		reflex die when: energy <= 0 {
			do action: die ;
		}
		
		reflex reproduce when: (energy >= energy_reproduce) and (flip(proba_reproduce)) {
			let nb_offsprings type: int value: 1 + rnd(nb_max_offsprings -1);
			create species: species(self) number: nb_offsprings {
				set myPatch var: myPatch value: myself.myPatch ;
				set location var: location value: myself.location + {0.5 - rnd(1000)/1000, 0.5 - rnd(1000)/1000} ;
				set energy value: myself.energy / nb_offsprings ;
			}
			set energy value: energy /nb_offsprings;
		}
		aspect base {
			draw shape: circle size: size color: color ;
		}
		aspect icon {
			draw image: my_icon size: size ;
		}
		aspect info {
			draw shape: square size: size color: color ;
			draw text: energy with_precision 2 size: 3 color: rgb('black') ;
		}
	}
	species prey parent: generic_species {
		const color type: rgb init: 'blue' ;
		const max_energy type: float init: prey_max_energy ;
		const max_transfert type: float init: prey_max_transfert ;
		const energy_consum type: float init: prey_energy_consum ;
		const energy_reproduce type: float init: prey_energy_reproduce ;
		const proba_reproduce type: float init: prey_proba_reproduce ;
		const nb_max_offsprings type: int init: prey_nb_max_offsprings ;
		const my_icon type: string init: '../includes/data/sheep.png' ;
		var speed type: float value: prey_speed;
		reflex eat when: myPatch.food > 0 {
			let energy_transfert value: min [max_transfert, myPatch.food] ;
			set myPatch.food value: myPatch.food - energy_transfert ;
			set energy value: energy + energy_transfert ;
		}
	}
	species predator parent: generic_species {
		const color type: rgb init: 'red' ;
		const max_energy type: float init: predator_max_energy ;
		const energy_transfert type: float init: predator_energy_transfert ;
		const energy_consum type: float init: predator_energy_consum ;
		const energy_reproduce type: float init: predator_energy_reproduce ;
		const proba_reproduce type: float init: predator_proba_reproduce ;
		const nb_max_offsprings type: int init: predator_nb_max_offsprings ;
		const my_icon type: string init: '../includes/data/wolf.png' ;
		var speed type: float value: predator_speed;
		reflex eat when: !(empty ((self neighbours_at predator_range) of_species prey)) {
			ask target: one_of ((self neighbours_at predator_range) of_species prey) {
				do action: die ;
			}
			set energy value: energy + energy_transfert ;
		}
	}
	species vegetation skills: situated {
		var max_food type: float value: 100 ;
		var foodProd type: float ;
		var food type: float value: min [max_food, food + foodProd] ;
		var color type: rgb value: [255 * ((max_food - food) / max_food), 255, 255 * ((max_food - food) / max_food)] ;
		aspect base {
			draw shape: geometry color: color ;
		}
	}

}

environment bounds: map_init ;

output {
	display main_display {
		image name: '../includes/data/soil.jpg' ;
		species vegetation aspect: base ;
		species prey aspect: base ;
		species predator aspect: base ;
	}
	display Population_information refresh_every: 5 {
		chart name: 'Species evolution' type: series background: rgb('white') size: {1,0.4} position: {0, 0.05} {
			data number_of_preys value: nb_preys color: rgb('blue') ;
			data number_of_predator value: nb_predators color: rgb('red') ;
		}
		chart name: 'Prey Energy Distribution' type: histogram background: rgb('lightGray') size: {0.5,0.4} position: {0, 0.5} {
			data name:"]0;0.25]" value: (prey as list) count (each.energy <= 0.25) ;
			data name:"]0.25;0.5]" value: (prey as list) count ((each.energy > 0.25) and (each.energy <= 0.5)) ;
			data name:"]0.5;0.75]" value: (prey as list) count ((each.energy > 0.5) and (each.energy <= 0.75)) ;
			data name:"]0.75;1]" value: (prey as list) count (each.energy > 0.75) ;
		}
		chart name: 'Predator Energy Distribution' type: histogram background: rgb('lightGray') size: {0.5,0.4} position: {0.5, 0.5} {
			data name:"]0;0.25]" value: (predator as list) count (each.energy <= 0.25) ;
			data name:"]0.25;0.5]" value: (predator as list) count ((each.energy > 0.25) and (each.energy <= 0.5)) ;
			data name:"]0.5;0.75]" value: (predator as list) count ((each.energy > 0.5) and (each.energy <= 0.75)) ;
			data name:"]0.75;1]" value: (predator as list) count (each.energy > 0.75) ;
		}
	}
	file name: 'results' type: text data: 'cycle: '+ time
					            + '; nbPreys: ' + nb_preys
					            + '; minEnergyPreys: ' + ((prey as list) min_of each.energy)
							    + '; maxSizePreys: ' + ((prey as list) max_of each.energy) 
   							    + '; nbPredators: ' + nb_predators           
   							    + '; minEnergyPredators: ' + ((predator as list) min_of each.energy)          
   							    + '; maxSizePredators: ' + ((predator as list) max_of each.energy) ;
	monitor number_of_preys value: nb_preys refresh_every: 1 ;
	monitor number_of_predators value: nb_predators refresh_every: 1 ;
}
