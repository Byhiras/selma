/*
 * Copyright 2013  Séven Le Mesle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package fr.xebia.extras.selma.it.custom.fields;

import fr.xebia.extras.selma.Selma;
import fr.xebia.extras.selma.beans.Passenger;
import fr.xebia.extras.selma.beans.Proposal;
import fr.xebia.extras.selma.beans.ProposalDto;
import fr.xebia.extras.selma.it.utils.Compile;
import fr.xebia.extras.selma.it.utils.IntegrationTestBase;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * Created by slemesle on 21/11/14.
 */
@Compile(withClasses = {
        EmbeddedFieldToUpperFieldMapper.class
})
public class EmbeddedFieldToUpperFieldInMapsIT extends IntegrationTestBase {


    @Test
    public void given_a_mapper_with_custom_embeded_field_to_upper_field_mapping_should_use_described_name_to_name_for_mapping() throws Exception {
        // Given
        EmbeddedFieldToUpperFieldMapper mapper = Selma.mapper(EmbeddedFieldToUpperFieldMapper.class);

        Proposal proposal = new Proposal();
        proposal.setArrivalDate(new Date());
        proposal.setDepartureDate(new Date());
        proposal.setOrigin("Paris");
        proposal.setDestination("Lyon");
        proposal.setPrice(1000);
        proposal.setPassenger(new Passenger());
        proposal.getPassenger().setAge(26);
        proposal.getPassenger().setCard("YOUNG");
        Date passengerDate = new Date();
        proposal.getPassenger().setDate(passengerDate);

        // When
        ProposalDto proposalDto = mapper.asProposalDto(proposal);

        // Then
        Assert.assertEquals(26, proposalDto.getPassengerAge());
        Assert.assertEquals("YOUNG", proposalDto.getPassengerCard());
        Assert.assertEquals(passengerDate, proposalDto.getPassengerDate());
    }


    @Test
    public void given_a_mapper_with_custom_upper_field_to_embedded_field_mapping_should_use_described_name_to_name_for_mapping() throws Exception {
        // Given
        EmbeddedFieldToUpperFieldMapper mapper = Selma.mapper(EmbeddedFieldToUpperFieldMapper.class);

        ProposalDto proposal = new ProposalDto();
        proposal.setArrivalDate(new Date());
        proposal.setDepartureDate(new Date());
        proposal.setOrigin("Paris");
        proposal.setDestination("Lyon");
        proposal.setPrice(1000);
        proposal.setPassengerAge(26);
        proposal.setPassengerCard("YOUNG");
        Date passengerDate = new Date();
        proposal.setPassengerDate(passengerDate);

        // When
        Proposal resProposal = mapper.asProposal(proposal);

        // Then
        Assert.assertNotNull(resProposal.getPassenger());
        Assert.assertEquals(26, resProposal.getPassenger().getAge());
        Assert.assertEquals("YOUNG", resProposal.getPassenger().getCard());
        Assert.assertEquals(passengerDate, resProposal.getPassenger().getDate());
    }

}
