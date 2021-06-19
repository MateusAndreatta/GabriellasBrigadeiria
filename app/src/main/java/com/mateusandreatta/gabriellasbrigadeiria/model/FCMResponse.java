package com.mateusandreatta.gabriellasbrigadeiria.model;

import java.util.List;

class Result{
    public String message_id;
}

public class FCMResponse{
    public long multicast_id;
    public int success;
    public int failure;
    public int canonical_ids;
    public List<Result> results;
}
