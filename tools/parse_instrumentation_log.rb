#!/usr/bin/ruby

require 'rexml/document'
include REXML

class TestResult
	def initialize(test_suite, test_case, test_result, test_stack = "")
		@test_suite=test_suite
		@test_case=test_case
		@test_result=test_result
		@test_stack=test_stack
	end
	def get_suite
		@test_suite.chomp
	end
	def get_name
		@test_case.chomp
	end
	def get_stack
		@test_stack
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
		test_stack=""

		raw_log.each do |line|
			if parsing_state == :searching_stack && line =~ /INSTRUMENTATION_STATUS:(.*)/ 
				parsing_state=:searching_test
			end
		
			if line =~ /INSTRUMENTATION_STATUS: stream=/
				parsing_state = :searching_test
			elsif parsing_state == :searching_test && line =~ /INSTRUMENTATION_STATUS: test=(.*)/
				test_case=$1
			elsif parsing_state == :searching_test && line =~ /INSTRUMENTATION_STATUS: class=(.*)/
				test_suite=$1
			elsif parsing_state == :searching_test && line =~ /INSTRUMENTATION_STATUS: stack=(.*)/
				test_stack=$1+"\n"
				parsing_state=:searching_stack
			elsif parsing_state == :searching_stack
				test_stack = test_stack + line + "\n"
			elsif parsing_state == :searching_test && line =~ /INSTRUMENTATION_STATUS_CODE: (.*)/
				test_status=$1
				
				test_result = TestResult.new(test_suite, test_case, test_status, test_stack)
				if test_status.to_i != 1
					@test_results << test_result
				end
				
				test_stack=""
				parsing_state=:start
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
		
		testrun_node = @xml_doc.add_element("testsuites", {"name" => "Rabbit Reminder", "tests" => test_results.size.to_s, "started" => test_results.size.to_s, "failures" => "0", "errors" => "0", "ignored" => "0" })
		
		
		last_test_suite=""
		testsuite_node=nil
		test_results.each do |test_case|
			if test_case.get_suite != last_test_suite
				testsuite_node=testrun_node.add_element("testsuite", {"name" => test_case.get_suite, "time" => ""})
				last_test_suite=test_case.get_suite
			end
			testcase_node=testsuite_node.add_element("testcase", {"name" => test_case.get_name, "classname" => test_case.get_suite, "time" => ""})
			if test_case.failure?
				failure_node = testcase_node.add_element("failure")
				failure_node.add_text(test_case.get_stack)
			end
			if test_case.error?
				error_node = testcase_node.add_element("error")
				error_node.add_text(test_case.get_stack)
			end
		end
	end
	def get_document
		@xml_doc
	end
end


if ARGV.size != 2
	puts "Syntax: ruby parse_instrumentation_log.rb <LOG FILENAME> <OUTPUT FILENAME>"
elsif
	f = open(ARGV[0], 'r')
	raw_log = f.read()

	p = Parser.new(raw_log)
	xml_gen = XmlGenerator.new(p.get_results)

	f_out = open(ARGV[1], 'w')
	xml_gen.get_document.write(f_out, 2)
end



