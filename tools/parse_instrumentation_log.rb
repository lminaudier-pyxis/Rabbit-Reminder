#!/usr/bin/ruby

require 'rexml/document'
include REXML

class TestResult
	def initialize(test_suite, test_case, test_result)
		@test_suite=test_suite
		@test_case=test_case
		@test_result=test_result
	end
	def get_suite
		@test_suite.chomp
	end
	def get_name
		@test_case.chomp
	end
	def successful?
		successful=true
		test_status=@test_result.to_i
		if test_status < 0
			successful=false
		end
		successful
	end
	def failure?
		@test_result.to_i == -2
	end
	def error?
		@test_result.to_i == -1
	end
	def display
		puts @test_suite
		puts @test_case
		puts successful?
	end
end

class Parser
	def initialize(raw_log)
		@test_results=[]
		
		parsing_state = :start
		test_case=""
		test_suite=""

		raw_log.each do |line|
			if line =~ /INSTRUMENTATION_STATUS: stream=/
				parsing_state = :searching_test
			elsif parsing_state == :searching_test && line =~ /INSTRUMENTATION_STATUS: test=(.*)/
				test_case=$1
				parsing_state=:searching_info
			elsif parsing_state == :searching_test
				test_suite=line
				parsing_state=:searching_info
			elsif parsing_state == :searching_info && line =~ /INSTRUMENTATION_STATUS_CODE: (.*)/
				test_status=$1
				parsing_state=:start
				
				test_result = TestResult.new(test_suite, test_case, test_status)
				if test_status.to_i != 1
					@test_results << test_result
				end
			end
		end
	end
	def get_results
		@test_results
	end
end

class XmlGenerator
	def initialize(test_results)
		@xml_doc = Document.new
		@xml_doc << XMLDecl.new
		
		testrun_node = @xml_doc.add_element("testrun", {"name" => "Rabbit Reminder", "tests" => test_results.size.to_s, "started" => test_results.size.to_s, "failures" => "0", "errors" => "0", "ignored" => "0" })
		
		
		last_test_suite=""
		testsuite_node=nil
		test_results.each do |test_case|
			if test_case.get_suite != last_test_suite
				testsuite_node=testrun_node.add_element("testsuite", {"name" => test_case.get_suite, "time" => ""})
				last_test_suite=test_case.get_suite
			end
			testcase_node=testsuite_node.add_element("testcase", {"name" => test_case.get_name, "classname" => test_case.get_suite, "time" => ""})
			if test_case.failure?
				testcase_node.add_element("failure")
			end
			if test_case.error?
				testcase_node.add_element("error")
			end
		end
	end
	def get_document
		@xml_doc
	end
end

f = open('log.txt', 'r')
raw_log = f.read()

p = Parser.new(raw_log)
xml_gen = XmlGenerator.new(p.get_results)

f_out = open('log_xunit.xml', 'w')
xml_gen.get_document.write(f_out, 2)

