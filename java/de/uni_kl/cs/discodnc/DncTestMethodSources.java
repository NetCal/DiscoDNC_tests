/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2017 - 2018 Steffen Bondorf
 * Copyright (C) 2017+ The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://discodnc.cs.uni-kl.de
 *
 *
 * The Disco Deterministic Network Calculator (DiscoDNC) is free software;
 * you can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software Foundation; 
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package de.uni_kl.cs.discodnc;

import de.uni_kl.cs.discodnc.curves.Curve;
import de.uni_kl.cs.discodnc.curves.LinearSegment;
import de.uni_kl.cs.discodnc.curves.dnc.LinearSegment_DNC;
import de.uni_kl.cs.discodnc.curves.dnc.affine.Curve_DNC_Affine;
import de.uni_kl.cs.discodnc.curves.dnc.pwaffine.Curve_DNC_PwAffine;
import de.uni_kl.cs.discodnc.minplus.MinPlus;
import de.uni_kl.cs.discodnc.minplus.dnc.affine.MinPlus_DNC_Affine;
import de.uni_kl.cs.discodnc.minplus.dnc.pwaffine.MinPlus_DNC_PwAffine;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.ArrivalBoundMethod;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.Multiplexing;
import de.uni_kl.cs.discodnc.numbers.NumBackend;

import org.junit.jupiter.params.provider.Arguments;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

//import CalculatorConfig.OperationImpl;

public class DncTestMethodSources {

	protected static Set<Set<ArrivalBoundMethod>> ab_sets = instantiateABsets();
	protected static Set<ArrivalBoundMethod> single_1, single_2, single_3, pair_1, pair_2, pair_3, triplet, sinktree;

	private static Set<Set<ArrivalBoundMethod>> instantiateABsets() {
		single_1 = new HashSet<ArrivalBoundMethod>();
		single_1.add(ArrivalBoundMethod.PBOO_CONCATENATION);

		single_2 = new HashSet<ArrivalBoundMethod>();
		single_2.add(ArrivalBoundMethod.PBOO_PER_HOP);

		single_3 = new HashSet<ArrivalBoundMethod>();
		single_3.add(ArrivalBoundMethod.PMOO);

		pair_1 = new HashSet<ArrivalBoundMethod>();
		pair_1.add(ArrivalBoundMethod.PBOO_PER_HOP);
		pair_1.add(ArrivalBoundMethod.PBOO_CONCATENATION);

		pair_2 = new HashSet<ArrivalBoundMethod>();
		pair_2.add(ArrivalBoundMethod.PBOO_PER_HOP);
		pair_2.add(ArrivalBoundMethod.PMOO);

		pair_3 = new HashSet<ArrivalBoundMethod>();
		pair_3.add(ArrivalBoundMethod.PBOO_CONCATENATION);
		pair_3.add(ArrivalBoundMethod.PMOO);

		triplet = new HashSet<ArrivalBoundMethod>();
		triplet.add(ArrivalBoundMethod.PBOO_PER_HOP);
		triplet.add(ArrivalBoundMethod.PBOO_CONCATENATION);
		triplet.add(ArrivalBoundMethod.PMOO);
		
		Set<Set<ArrivalBoundMethod>> ab_sets = new HashSet<Set<ArrivalBoundMethod>>();
		ab_sets.add(single_1);
		ab_sets.add(single_2);
		ab_sets.add(single_3);
		ab_sets.add(pair_1);
		ab_sets.add(pair_2);
		ab_sets.add(pair_3);
		ab_sets.add(triplet);

		// sink tree bounds are not added to ab_sets as the tests treat them differently. 
		sinktree = new HashSet<ArrivalBoundMethod>();
		sinktree.add(ArrivalBoundMethod.PMOO_SINKTREE_TBRL);
		sinktree.add(ArrivalBoundMethod.PMOO_SINKTREE_TBRL_CONV);
		sinktree.add(ArrivalBoundMethod.PMOO_SINKTREE_TBRL_CONV_TBRL_DECONV);
		sinktree.add(ArrivalBoundMethod.PMOO_SINKTREE_TBRL_HOMO);
		
		return ab_sets;
	}

	public static Stream<Arguments> provideAllArguments() {
		return Stream.concat(provideArbArguments(), provideFifoArguments());
	}
	
	protected static Stream<Arguments> provideArbArguments() {
		return createParameters(Collections.singleton(Multiplexing.ARBITRARY), ab_sets).stream().map(Arguments::of);
	}
	
	protected static Stream<Arguments> provideFifoArguments() {
		Set<Set<ArrivalBoundMethod>> ab_sets_fifo = new HashSet<Set<ArrivalBoundMethod>>();
		ab_sets_fifo.add(single_1);
		ab_sets_fifo.add(single_2);
		ab_sets_fifo.add(pair_1);
		
		return createParameters(Collections.singleton(Multiplexing.FIFO), ab_sets_fifo).stream().map(Arguments::of);
	}
	
	protected static Stream<Arguments> provideSinkTreeArguments() {
		Set<Set<ArrivalBoundMethod>> ab_sets_sinktree = new HashSet<Set<ArrivalBoundMethod>>();
		ab_sets_sinktree.add(sinktree); // TODO Test powerset of sinktree alternatives
		
		return createParameters(Collections.singleton(Multiplexing.ARBITRARY), ab_sets_sinktree).stream().map(Arguments::of);
	}

	private static Set<DncTestConfig> createParameters(Set<Multiplexing> mux_disciplines, Set<Set<ArrivalBoundMethod>> ab_sets) {
		Set<DncTestConfig> test_configurations = new HashSet<DncTestConfig>();

		Set<NumBackend> nums = new HashSet<NumBackend>();
		nums.add(NumBackend.REAL_DOUBLE_PRECISION);
		nums.add(NumBackend.REAL_SINGLE_PRECISION);
		nums.add(NumBackend.RATIONAL_INTEGER);
		nums.add(NumBackend.RATIONAL_BIGINTEGER);

		Set<CurveBackend> curves = new HashSet<CurveBackend>();
		curves.add(CurveBackend_DNC_PwAffine.DNC_PWAFFINE);
		curves.add(CurveBackend_DNC_Affine.DNC_AFFINE);
		curves.add(de.uni_kl.cs.discodnc.CurveBackend_MPARTC.MPARTC);
		curves.add(CurveBackend_DNC_AffineC_PwAffineMP.DNC_AFFINEC_PWAFFINEMP);
		curves.add(CurveBackend_DNC_PwAffineC_AffineMP.DNC_PWAFFINEC_AFFINEMP);

		// Parameter configurations for single arrival bounding tests:
		// 		AB, convolve alternative ABs, global mux def, number class to use, curve class to use.
		for (CurveBackend curve : curves) {
			for (NumBackend num : nums) {
				for (Set<ArrivalBoundMethod> ab : ab_sets) {
					for (Multiplexing mux : mux_disciplines) {
						test_configurations.add(new DncTestConfig(ab, false, mux, false, num, curve));
						test_configurations.add(new DncTestConfig(ab, true, mux, false, num, curve));
						test_configurations.add(new DncTestConfig(ab, false, mux, true, num, curve));
						test_configurations.add(new DncTestConfig(ab, true, mux, true, num, curve));
					}
				}
			}
		}

		return test_configurations;
	}
}

enum CurveBackend_DNC_AffineC_PwAffineMP implements CurveBackend {
	DNC_AFFINEC_PWAFFINEMP;

	@Override
	public MinPlus getMinPlus() {
		return MinPlus_DNC_PwAffine.MINPLUS_DNC;
	}

	@Override
	public Curve getCurveFactory() {
		return Curve_DNC_Affine.getFactory();
	}

	@Override
	public LinearSegment.Builder getLinearSegmentFactory() {
		return LinearSegment_DNC.getBuilder();
	}

    @Override
    public String toString() {
    	 StringBuffer curve_backend_str = new StringBuffer();

         curve_backend_str.append("Curve");
         curve_backend_str.append(":");
         curve_backend_str.append(this.name());
         
         curve_backend_str.append(", ");

         curve_backend_str.append("Operations");
         curve_backend_str.append(":");
         curve_backend_str.append(MinPlus_DNC_PwAffine.MINPLUS_DNC.name());

         return curve_backend_str.toString();
    }
}

enum CurveBackend_DNC_PwAffineC_AffineMP implements CurveBackend {
	DNC_PWAFFINEC_AFFINEMP;

	@Override
	public MinPlus getMinPlus() {
		return MinPlus_DNC_Affine.MINPLUS_DNC_AFFINE;
	}

	@Override
	public Curve getCurveFactory() {
		return Curve_DNC_PwAffine.getFactory();
	}

	@Override
	public LinearSegment.Builder getLinearSegmentFactory() {
		return LinearSegment_DNC.getBuilder();
	}

    @Override
    public String toString() {
    	 StringBuffer curve_backend_str = new StringBuffer();

         curve_backend_str.append("Curve");
         curve_backend_str.append(":");
         curve_backend_str.append(this.name());
         
         curve_backend_str.append(", ");

         curve_backend_str.append("Operations");
         curve_backend_str.append(":");
         curve_backend_str.append(MinPlus_DNC_PwAffine.MINPLUS_DNC.name());

         return curve_backend_str.toString();
    }
}