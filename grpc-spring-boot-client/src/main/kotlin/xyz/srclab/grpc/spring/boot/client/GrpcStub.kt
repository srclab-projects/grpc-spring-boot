package xyz.srclab.grpc.spring.boot.client

import io.grpc.stub.AbstractStub

interface GrpcStub<S : AbstractStub<S>> {

    fun get(): S
}