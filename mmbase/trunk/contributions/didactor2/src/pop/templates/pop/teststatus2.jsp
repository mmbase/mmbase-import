<%--
    input params:
        testNo - number of the intake node
        copybookNo - number of the students relevant copybook

    sets: teststatus to

        "passed" if the intake was passed
        "failed" if the intake was failed
--%>
	<mm:import id="teststatus" reset="true">incomplete</mm:import>
    <mm:list nodes="$testNo" path="tests,intakeresults,copybooks" constraints="copybooks.number='$copybookNo'">
		<mm:field name="intakeresults.score">
	        <mm:compare value="1">
				<mm:import id="teststatus" reset="true">passed</mm:import>
            </mm:compare>
	        <mm:compare value="1" inverse="true">
				<mm:import id="teststatus" reset="true">failed</mm:import>
            </mm:compare>
        </mm:field>
    </mm:list>
